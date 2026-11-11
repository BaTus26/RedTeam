# Note: Đọc code nhanh bằng grep, tra CVE, học từ chuyên gia

> Nguyên tắc: **đi từ sink (hàm nguy hiểm) ngược về source (input người dùng)**. Không đọc hết code.
> Dùng `rg` (ripgrep) cho nhanh. Nếu chưa có: `sudo apt install ripgrep`. Có thể thay `rg -n` bằng `grep -rnE`.

---

## 0. Quy trình 15 phút khi gặp source lạ

1. `ls`, `tree -L 2` để xem cấu trúc, đoán ngôn ngữ và framework.
2. Đọc file cấu hình, README, Dockerfile, docker-compose, `.env`.
3. Liệt kê routes (xem mục 2) để biết các điểm vào.
4. Grep secrets (mục 1) và sink theo ngôn ngữ (mục 3).
5. Với mỗi kết quả, hỏi 3 câu: input của user có chảy tới đây không? Có bị lọc/escape không? Khai thác được không?
6. Nếu 15-20 phút chưa ra hướng, quay lại enumeration, đừng cố đọc hết.

---

## 1. Grep chung (mọi ngôn ngữ)

```bash
# Secrets, credentials
rg -n -i "password|passwd|secret|api[_-]?key|token|private[_-]?key|BEGIN (RSA|OPENSSH|EC) PRIVATE"

# Ghi chú của dev (hay lộ thông tin)
rg -n "TODO|FIXME|HACK|XXX|DEBUG|test|backdoor"

# Phân quyền, xác thực (nơi hay có lỗi logic)
rg -n -i "isAdmin|is_admin|role|authorize|permission|jwt|session|csrf"

# Gọi ra ngoài (SSRF), redirect, file
rg -n -i "redirect|url=|fetch\(|curl|requests\.(get|post)|http\.Get|file_get_contents|readFile|upload"

# Crypto yếu
rg -n -i "md5|sha1|base64|rand\(|Math\.random|ECB|DES"
```

### Lịch sử git (thường lộ thứ đã xóa)

```bash
git log --oneline --all
git log -p -S"password"        # tìm commit có thêm/xóa chuỗi "password"
git branch -a
git show <commit>
gitleaks detect -v             # quét secrets trong history
trufflehog git file://. 
```

---

## 2. Tìm routes (các điểm vào)

| Framework | Grep |
|---|---|
| Flask | `rg -n "@app\.route\|@bp\.route"` |
| Django | `rg -n "path\(\|re_path\(" -g 'urls.py'` |
| Express | `rg -n "app\.(get\|post\|put\|delete)\|router\.(get\|post)"` |
| Spring | `rg -n "@(Get\|Post\|Put\|Delete\|Request)Mapping"` |
| Laravel | `rg -n "Route::" routes/` |
| Rails | `cat config/routes.rb` |
| PHP thuần | mỗi file `.php` là một route, tìm `rg -n '\$_(GET\|POST\|REQUEST\|COOKIE\|FILES)'` |
| Go | `rg -n "HandleFunc\|\.GET\(\|\.POST\("` |
| ASP.NET | `rg -n "\[Http(Get\|Post)\]\|MapControllerRoute"` |

---

## 3. Grep sink theo ngôn ngữ

### PHP
```bash
rg -n "eval\(|assert\(|system\(|exec\(|shell_exec|passthru|popen|proc_open|unserialize\(" -g '*.php'
rg -n "(include|require)(_once)?\s*\(?\s*\\$" -g '*.php'          # LFI/RFI
rg -n "move_uploaded_file|file_put_contents|file_get_contents\(\s*\\$" -g '*.php'
rg -n "mysqli?_query|->query\(|\"\s*\.\s*\\$" -g '*.php'           # SQLi nối chuỗi
rg -n "extract\(|parse_str\(|preg_replace.*/e" -g '*.php'
```
Lỗi hay gặp: LFI, RCE qua upload, SQLi, deserialization, type juggling (`==` so sánh lỏng).

### Python
```bash
rg -n "eval\(|exec\(|os\.system|os\.popen|subprocess|__import__" -g '*.py'
rg -n "pickle\.loads?|yaml\.load\(|marshal\.loads|shelve" -g '*.py'
rg -n "render_template_string|\.format\(|f\".*\{.*request" -g '*.py'   # SSTI
rg -n "execute\(.*(%|\+|format|f\")" -g '*.py'                          # SQLi
rg -n "request\.(args|form|json|files|cookies|headers)" -g '*.py'
```
Lỗi hay gặp: SSTI (Jinja2), pickle deserialization, command injection, SQLi nối chuỗi.

### Node.js / JavaScript / TypeScript
```bash
rg -n "eval\(|new Function|vm\.run|child_process|execSync|\.exec\(|spawn\(" -g '*.js' -g '*.ts'
rg -n "innerHTML|outerHTML|document\.write|dangerouslySetInnerHTML|v-html" -g '*.js' -g '*.ts' -g '*.jsx' -g '*.vue'
rg -n "req\.(query|body|params|headers|cookies)" -g '*.js' -g '*.ts'
rg -n "__proto__|Object\.assign|merge\(|extend\(|lodash" -g '*.js' -g '*.ts'   # prototype pollution
rg -n "deserialize|unserialize|node-serialize" -g '*.js'
```
Lỗi hay gặp: prototype pollution, XSS DOM, command injection, NoSQL injection, SSRF.
**Với bug bounty:** đọc JS phía client để tìm endpoint ẩn, API key, logic kiểm tra phía client:
```bash
rg -n -o "https?://[a-zA-Z0-9./?=_%:-]*" app.js          # URL trong JS
rg -n -i "api|endpoint|/v1/|/admin|internal" app.js
```

### Java
```bash
rg -n "Runtime\.getRuntime|ProcessBuilder|ScriptEngine" -g '*.java'
rg -n "ObjectInputStream|readObject|XMLDecoder|XStream|SnakeYAML|Yaml\(\)" -g '*.java'   # deserialization
rg -n "createStatement|executeQuery|executeUpdate|\"\s*\+\s*" -g '*.java'                 # SQLi
rg -n "getParameter|@RequestParam|@PathVariable|@RequestBody" -g '*.java'
rg -n "DocumentBuilderFactory|SAXParser|XMLInputFactory" -g '*.java'                       # XXE
rg -n "InitialContext|\.lookup\(" -g '*.java'                                              # JNDI (kiểu Log4Shell)
```

### Ruby / Rails
```bash
rg -n "eval\(|system\(|exec\(|\`|%x\(|open\(|send\(|constantize" -g '*.rb'
rg -n "Marshal\.load|YAML\.load" -g '*.rb'
rg -n "html_safe|raw\(|params\[" -g '*.rb' -g '*.erb'
rg -n "where\(.*#\{|find_by_sql" -g '*.rb'              # SQLi
```

### Go
```bash
rg -n "exec\.Command|template\.HTML|text/template" -g '*.go'
rg -n "fmt\.Sprintf\(.*(SELECT|INSERT|UPDATE|DELETE)" -g '*.go'   # SQLi
rg -n "http\.Get\(|os\.Open\(|filepath\.Join\(" -g '*.go'         # SSRF, path traversal
rg -n "InsecureSkipVerify|math/rand" -g '*.go'
```

### C# / .NET
```bash
rg -n "Process\.Start|BinaryFormatter|XmlSerializer|JavaScriptSerializer|TypeNameHandling" -g '*.cs'
rg -n "SqlCommand|ExecuteReader|\"\s*\+\s*" -g '*.cs'
rg -n "Request\.(QueryString|Form|Params)|Html\.Raw" -g '*.cs' -g '*.cshtml'
```

### C / C++ (memory bugs)
```bash
rg -n "strcpy|strcat|sprintf|gets\(|scanf|memcpy|system\(" -g '*.c' -g '*.cpp'
rg -n "printf\([a-zA-Z_]+\)" -g '*.c'      # format string
```

---

## 4. Công cụ quét tự động (lọc bớt cho nhanh)

```bash
semgrep --config auto .                      # quét nhiều ngôn ngữ cùng lúc, học cái này đầu tiên
semgrep --config p/owasp-top-ten .
gitleaks detect -v                           # secrets
npm audit                                    # thư viện Node
pip-audit                                    # thư viện Python
osv-scanner -r .                             # thư viện đa ngôn ngữ (dùng OSV.dev)
trivy fs .                                   # thư viện + cấu hình + container
```
Kết quả của tool chỉ là gợi ý, luôn tự kiểm tra lại có khai thác được không.

---

## 5. Cách tra CVE

### Quy trình
1. Tìm **tên + phiên bản** (từ banner, `package.json`, `requirements.txt`, `pom.xml`, header, trang lỗi).
2. Tra: `"<tên> <phiên bản> CVE"` hoặc `searchsploit <tên> <phiên bản>`.
3. Đọc advisory để hiểu **lỗi nằm ở đâu, điều kiện khai thác là gì**.
4. Nếu có bản vá trên GitHub, đọc **commit diff** để thấy chính xác chỗ sửa (patch diffing). Đây là cách học code review hiệu quả nhất.
5. Xem điểm **CVSS** (độ nặng) và **EPSS** (khả năng bị khai thác thật). Kiểm tra có trong **CISA KEV** không (đang bị khai thác ngoài thực tế).

### Nguồn nên đọc
| Nguồn | Dùng để |
|---|---|
| NVD (nvd.nist.gov) và CVE.org | Thông tin CVE chính thức |
| GitHub Advisory Database (github.com/advisories) | Advisory kèm link commit sửa lỗi |
| OSV.dev | Tra lỗ hổng theo thư viện và phiên bản |
| CISA KEV | Danh sách CVE đang bị khai thác thật |
| Exploit-DB và `searchsploit` (chạy offline trên Kali) | PoC, exploit có sẵn |
| EPSS (first.org/epss) | Xác suất bị khai thác |
| Snyk Vulnerability DB | Lỗi trong thư viện npm, pip, maven |
| AttackerKB (Rapid7) | Đánh giá và thảo luận mức độ khai thác |
| Nuclei templates (GitHub của ProjectDiscovery) | Xem cách phát hiện CVE bằng template, tự quét |
| Packet Storm | Exploit và advisory |

Mẹo: đọc **write-up phân tích CVE** (tìm "CVE-xxxx-xxxx analysis/root cause") hơn là chỉ chạy PoC. Hiểu root cause thì bạn nhận ra lỗi tương tự ở chỗ khác.

---

## 6. Học từ chuyên gia và hacker

### Người và kênh nên theo dõi
- **IppSec** (YouTube): giải HTB từng bước, xem cách tư duy khi bí.
- **0xdf** (0xdf.gitlab.io): write-up HTB rất chi tiết.
- **PortSwigger Web Security Academy và Research**: học web miễn phí, bài nghiên cứu của James Kettle rất đáng đọc.
- **Tib3rius**: khóa học privesc, hay chia sẻ mẹo trên mạng xã hội.
- **STÖK, NahamSec, Jason Haddix**: video bug bounty, recon, methodology (xem bài "Bug Hunter's Methodology" của Jason Haddix).
- **Orange Tsai, Frans Rosén, Assetnote Research**: nghiên cứu web nâng cao.
- **SonarSource Blog**: phân tích lỗi trong code thật, rất hợp để luyện code review.
- **Google Project Zero blog**: phân tích lỗi sâu (đọc khi đã vững).

### Tài liệu và trang tra cứu
- **HackTricks**: cheat sheet kỹ thuật đủ mảng.
- **PayloadsAllTheThings** (GitHub): payload cho từng loại lỗi.
- **OWASP Testing Guide và Cheat Sheet Series**: phương pháp và cách phòng chống (hiểu phòng chống để biết cách bypass).
- **HackerOne Hacktivity, Pentester Land, Bugcrowd/Intigriti blog**: báo cáo và write-up bug bounty thật.
- **Hacker101** (HackerOne): khóa học và CTF miễn phí cho bug bounty.
- **PentesterLab**: bài tập code review và web.
- Sách: *Bug Bounty Bootcamp* (Vickie Li), *The Web Application Hacker's Handbook*.

### Thói quen học hiệu quả
- Mỗi tuần đọc 2-3 write-up bug bounty và ghi lại: **lỗi gì, tìm thấy ở đâu, do thiếu kiểm tra gì**.
- Sau mỗi lab HTB, xem write-up của IppSec/0xdf để học cách làm khác.
- Ghi vào note riêng: lệnh grep, payload, lỗi hay gặp. Sau vài tháng sẽ thành cheat sheet của chính bạn.
- Gặp code lạ thì dán đúng đoạn đáng ngờ cho AI và hỏi: "input nào vào đây, có bị lọc không, khai thác thế nào".

---

## 7. Checklist nhanh khi review một route

- [ ] Input đến từ đâu (query, body, header, cookie, file)?
- [ ] Có kiểm tra đăng nhập và quyền (user A xem được dữ liệu user B không → IDOR)?
- [ ] Input đi vào đâu (SQL, lệnh hệ thống, đường dẫn file, URL, template, HTML)?
- [ ] Có validate/escape trước khi đi vào sink không, có thể bypass không?
- [ ] Có race condition, lỗi logic (giá âm, bỏ qua bước thanh toán, reset mật khẩu)?
