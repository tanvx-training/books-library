# Tài liệu Đặc tả Yêu cầu Phần mềm (SRS) - Hệ thống Thư viện Mượn Sách

## 1. Giới thiệu (Introduction)

### 1.1. Mục đích tài liệu (Purpose)

Tài liệu Đặc tả Yêu cầu Phần mềm (Software Requirements Specification - SRS) này nhằm mục đích mô tả chi tiết các yêu cầu chức năng và phi chức năng của Hệ thống Thư viện Mượn Sách. Tài liệu này đóng vai trò là cơ sở tham chiếu chính cho quá trình phát triển, kiểm thử và nghiệm thu hệ thống.

Đối tượng đọc chính của tài liệu này bao gồm:
*   **Đội ngũ phát triển (Development Team):** Bao gồm các lập trình viên, kỹ sư kiểm thử, kiến trúc sư hệ thống, để hiểu rõ các yêu cầu cần thực hiện.
*   **Quản lý dự án (Project Manager):** Để theo dõi tiến độ, quản lý phạm vi và nguồn lực dự án.
*   **Khách hàng/Bên liên quan (Stakeholders):** Bao gồm đại diện thư viện, người dùng cuối, để xác nhận rằng hệ thống đáp ứng đúng nhu cầu và mong đợi.
*   **Đội ngũ bảo trì (Maintenance Team):** Để hiểu rõ cấu trúc và chức năng của hệ thống khi cần bảo trì hoặc nâng cấp.

### 1.2. Phạm vi hệ thống (Scope)

Hệ thống Thư viện Mượn Sách là một ứng dụng web được xây dựng theo kiến trúc microservices, nhằm mục đích hiện đại hóa và tự động hóa các quy trình quản lý thư viện, bao gồm quản lý người dùng, quản lý sách, quản lý mượn/trả sách và hệ thống thông báo.

Mục tiêu chính của hệ thống bao gồm:
*   Cung cấp giao diện thân thiện cho độc giả để tìm kiếm, mượn, trả và đặt trước sách.
*   Hỗ trợ thủ thư và quản trị viên trong việc quản lý kho sách, quản lý người dùng, theo dõi tình trạng mượn/trả và tạo báo cáo.
*   Tự động hóa quy trình thông báo nhắc nhở trả sách, thông báo sách mới, thông báo phạt quá hạn.
*   Đảm bảo tính bảo mật, hiệu suất và khả năng mở rộng của hệ thống.

Hệ thống sẽ bao gồm các chức năng chính sau:
*   Quản lý người dùng (đăng ký, đăng nhập, phân quyền, quản lý thẻ thư viện).
*   Quản lý sách (thêm, sửa, xóa, tìm kiếm, quản lý danh mục, tác giả, nhà xuất bản).
*   Quản lý mượn/trả sách (ghi nhận mượn, trả, tính phí phạt, theo dõi lịch sử).
*   Quản lý đặt trước sách.
*   Hệ thống thông báo (email, SMS, push notification).

Các chức năng không thuộc phạm vi của hệ thống này bao gồm (nhưng không giới hạn): quản lý tài chính chi tiết của thư viện, quản lý nhân sự, tích hợp với các hệ thống thư viện số bên ngoài (trừ khi có yêu cầu cụ thể sau này).

### 1.3. Tài liệu tham khảo (References)

Các tài liệu sau đây được sử dụng làm tài liệu tham khảo trong quá trình xây dựng SRS này:
*   `/home/ubuntu/library-system/docs/requirements.md`: Tài liệu phân tích yêu cầu ban đầu.
*   `/home/ubuntu/library-system/docs/microservices-design.md`: Tài liệu thiết kế chi tiết các microservices và module hỗ trợ.
*   `/home/ubuntu/library-system/docs/system-architecture.md`: Tài liệu kiến trúc hệ thống tổng thể.
*   `/home/ubuntu/library-system/docs/design-validation.md`: Tài liệu kiểm tra và xác nhận thiết kế.
*   *Tiêu chuẩn IEEE 830-1998:* Recommended Practice for Software Requirements Specifications (nếu áp dụng).

### 1.4. Định nghĩa và từ viết tắt (Definitions, Acronyms, Abbreviations)

*   **SRS (Software Requirements Specification):** Tài liệu Đặc tả Yêu cầu Phần mềm.
*   **Microservices:** Kiến trúc phần mềm phân tách ứng dụng thành các dịch vụ nhỏ, độc lập.
*   **API (Application Programming Interface):** Giao diện lập trình ứng dụng, phương thức giao tiếp giữa các thành phần phần mềm.
*   **RESTful API:** Kiến trúc API sử dụng các nguyên tắc của REST (Representational State Transfer).
*   **Spring Boot:** Framework Java phổ biến để xây dựng ứng dụng, đặc biệt là microservices.
*   **Redis:** Hệ quản trị cơ sở dữ liệu key-value trong bộ nhớ, thường dùng cho caching và message brokering.
*   **Kafka (Apache Kafka):** Nền tảng phân tán xử lý luồng dữ liệu, thường dùng làm message broker.
*   **PostgreSQL:** Hệ quản trị cơ sở dữ liệu quan hệ đối tượng mã nguồn mở.
*   **Eureka Server:** Thành phần của Netflix OSS, dùng làm Service Discovery trong kiến trúc microservices.
*   **API Gateway:** Điểm vào duy nhất cho tất cả các client requests, định tuyến đến các microservice phù hợp.
*   **JWT (JSON Web Token):** Chuẩn mở (RFC 7519) định nghĩa cách truyền thông tin an toàn giữa các bên dưới dạng đối tượng JSON.
*   **DTO (Data Transfer Object):** Đối tượng dùng để truyền dữ liệu giữa các tiến trình hoặc lớp.
*   **Admin (Administrator):** Quản trị viên hệ thống, có quyền cao nhất.
*   **Librarian:** Thủ thư, người quản lý hoạt động hàng ngày của thư viện.
*   **Reader:** Độc giả, người dùng cuối sử dụng hệ thống để mượn sách.
*   **UI (User Interface):** Giao diện người dùng.
*   **UX (User Experience):** Trải nghiệm người dùng.
*   **CRUD:** Các thao tác cơ bản trong quản lý dữ liệu: Create, Read, Update, Delete.
*   **Load Balancing:** Phân phối tải công việc trên nhiều tài nguyên tính toán.
*   **Circuit Breaker:** Mẫu thiết kế dùng để phát hiện lỗi và ngăn chặn lỗi lan truyền trong hệ thống phân tán.
*   **Service Registry:** Cơ chế để các microservice đăng ký và khám phá lẫn nhau.
*   **Scalability:** Khả năng mở rộng của hệ thống để xử lý tải tăng lên.
*   **Availability:** Khả năng hệ thống luôn sẵn sàng hoạt động.
*   **Fault Tolerance:** Khả năng hệ thống tiếp tục hoạt động đúng cách ngay cả khi có lỗi xảy ra ở một hoặc nhiều thành phần.




## 2. Mô tả tổng quan (Overall Description)

Phần này cung cấp một cái nhìn tổng quan về Hệ thống Thư viện Mượn Sách, bao gồm bối cảnh hoạt động, các chức năng chính, đặc điểm của người dùng dự kiến, các ràng buộc kỹ thuật và các giả định quan trọng trong quá trình phát triển.

### 2.1. Bối cảnh sản phẩm (Product Perspective)

Hệ thống Thư viện Mượn Sách là một hệ thống độc lập, được thiết kế để hoạt động như một ứng dụng web hoàn chỉnh. Nó không phải là một phần của một hệ thống lớn hơn, tuy nhiên, nó được xây dựng dựa trên kiến trúc microservices, bao gồm nhiều thành phần tương tác với nhau.

Các thành phần chính của hệ thống bao gồm:
*   **User Service:** Quản lý thông tin người dùng và xác thực.
*   **Book Service:** Quản lý thông tin sách và các giao dịch mượn/trả.
*   **Notification Service:** Quản lý việc gửi thông báo.
*   **API Gateway:** Cổng vào duy nhất cho các yêu cầu từ client.
*   **Eureka Server:** Dịch vụ đăng ký và khám phá service.
*   **Common Library:** Thư viện chứa mã nguồn dùng chung.

Các microservice này tương tác với nhau thông qua API RESTful (đồng bộ) và Kafka (bất đồng bộ). Hệ thống sử dụng PostgreSQL làm cơ sở dữ liệu cho từng service và Redis cho mục đích caching.

Mặc dù là một hệ thống độc lập, Notification Service có thể phụ thuộc vào các dịch vụ bên ngoài để gửi thông báo qua SMS hoặc push notification (ví dụ: Twilio, Firebase Cloud Messaging). Việc tích hợp cụ thể với các dịch vụ này sẽ được xác định trong giai đoạn triển khai chi tiết.

Hệ thống được thiết kế để có thể tích hợp với các hệ thống khác trong tương lai thông qua các API được cung cấp bởi API Gateway, ví dụ như tích hợp với hệ thống quản lý sinh viên của một trường đại học hoặc hệ thống quản lý tài chính.

### 2.2. Chức năng tổng quát (Product Functions)

Hệ thống cung cấp một loạt các chức năng để hỗ trợ hoạt động của thư viện và phục vụ người dùng. Các nhóm chức năng chính bao gồm:

*   **Quản lý Người dùng:** Cho phép đăng ký tài khoản mới, đăng nhập, đăng xuất, quản lý thông tin cá nhân, xem lịch sử mượn/trả, và quản lý thẻ thư viện (gia hạn, báo mất). Hệ thống cũng hỗ trợ phân quyền cho các loại người dùng khác nhau (Độc giả, Thủ thư, Quản trị viên).
*   **Quản lý Sách:** Cho phép quản trị viên và thủ thư thêm, sửa, xóa thông tin sách, quản lý các danh mục, tác giả, nhà xuất bản. Độc giả có thể tìm kiếm sách theo nhiều tiêu chí (tên sách, tác giả, thể loại, ISBN), xem chi tiết thông tin sách và tình trạng sẵn có.
*   **Quản lý Mượn/Trả sách:** Hỗ trợ quy trình mượn sách (kiểm tra điều kiện mượn, ghi nhận giao dịch), trả sách (cập nhật trạng thái, tính phí phạt nếu có), và theo dõi lịch sử mượn/trả của từng người dùng và từng cuốn sách.
*   **Quản lý Đặt trước:** Cho phép độc giả đặt trước những cuốn sách đang được mượn. Hệ thống sẽ thông báo khi sách có sẵn.
*   **Hệ thống Thông báo:** Tự động gửi các thông báo quan trọng đến người dùng qua email (hoặc SMS/push notification nếu được cấu hình), bao gồm xác nhận mượn/trả, nhắc nhở trả sách, thông báo quá hạn, thông báo sách đặt trước có sẵn, thông báo về tài khoản (ví dụ: thẻ sắp hết hạn).
*   **Quản lý Hệ thống (Admin):** Cung cấp các công cụ cho quản trị viên để cấu hình hệ thống, quản lý người dùng, quản lý vai trò và quyền hạn, xem báo cáo thống kê.

(Sơ đồ Use Case tổng quát sẽ được cung cấp trong Phụ lục để minh họa rõ hơn các chức năng này và mối quan hệ giữa chúng).

### 2.3. Đặc điểm người dùng (User Characteristics)

Hệ thống có ba nhóm người dùng chính với các đặc điểm và nhu cầu khác nhau:

*   **Độc giả (Reader):**
    *   **Mô tả:** Là người dùng cuối cùng của hệ thống, bao gồm sinh viên, giảng viên, hoặc bất kỳ ai được cấp thẻ thư viện.
    *   **Trình độ kỹ thuật:** Đa dạng, từ người dùng cơ bản đến người có kinh nghiệm sử dụng máy tính và web. Giao diện cần trực quan, dễ sử dụng, không yêu cầu kiến thức kỹ thuật chuyên sâu.
    *   **Nhu cầu chính:** Tìm kiếm sách, xem thông tin sách, mượn sách, trả sách, đặt trước sách, quản lý tài khoản cá nhân, nhận thông báo.
*   **Thủ thư (Librarian):**
    *   **Mô tả:** Nhân viên thư viện chịu trách nhiệm vận hành hàng ngày.
    *   **Trình độ kỹ thuật:** Có kiến thức cơ bản về máy tính và sử dụng phần mềm văn phòng. Cần được đào tạo để sử dụng các chức năng quản lý của hệ thống.
    *   **Nhu cầu chính:** Quản lý sách (thêm, sửa, xóa), quản lý bản sao sách, xử lý mượn/trả sách tại quầy, quản lý thông tin độc giả (hỗ trợ đăng ký, cập nhật), theo dõi sách quá hạn, tạo báo cáo cơ bản.
*   **Quản trị viên (Administrator):**
    *   **Mô tả:** Người chịu trách nhiệm quản lý toàn bộ hệ thống.
    *   **Trình độ kỹ thuật:** Có kiến thức tốt về công nghệ thông tin và quản trị hệ thống.
    *   **Nhu cầu chính:** Quản lý tài khoản người dùng và phân quyền, cấu hình hệ thống (ví dụ: quy định mượn, phí phạt), quản lý danh mục, tác giả, nhà xuất bản, xem báo cáo thống kê chi tiết, giám sát hoạt động hệ thống.

### 2.4. Giới hạn (Constraints)

Quá trình phát triển và vận hành hệ thống phải tuân thủ các giới hạn sau:

*   **Ngôn ngữ lập trình:** Java (phiên bản LTS mới nhất, ví dụ: Java 17 hoặc 21).
*   **Framework:** Spring Boot (phiên bản ổn định mới nhất).
*   **Kiến trúc:** Microservices.
*   **Cơ sở dữ liệu:** PostgreSQL (phiên bản ổn định mới nhất) cho lưu trữ dữ liệu chính, Redis (phiên bản ổn định mới nhất) cho caching.
*   **Message Broker:** Apache Kafka (phiên bản ổn định mới nhất).
*   **Service Discovery:** Spring Cloud Netflix Eureka.
*   **API Gateway:** Spring Cloud Gateway.
*   **Giao thức giao tiếp:** HTTP/HTTPS cho API, TCP cho Kafka.
*   **Định dạng dữ liệu:** JSON cho API và Kafka messages.
*   **Xác thực:** JWT (JSON Web Token).
*   **Hệ điều hành:** Hệ thống phải có khả năng triển khai trên môi trường Linux (ví dụ: Ubuntu Server LTS).
*   **Giao diện người dùng:** Phải là ứng dụng web, tương thích với các trình duyệt hiện đại phổ biến (Chrome, Firefox, Safari, Edge) trên cả máy tính để bàn và thiết bị di động (thiết kế responsive).
*   **Bảo mật:** Tuân thủ các nguyên tắc bảo mật cơ bản như OWASP Top 10. Dữ liệu nhạy cảm (như mật khẩu) phải được mã hóa khi lưu trữ. Giao tiếp qua mạng nên sử dụng HTTPS.
*   **Ngôn ngữ giao diện:** Tiếng Việt (có thể hỗ trợ đa ngôn ngữ nếu có yêu cầu sau này).

### 2.5. Giả định và phụ thuộc (Assumptions and Dependencies)

Các giả định và phụ thuộc sau được đặt ra trong quá trình thiết kế và phát triển:

*   **Giả định:**
    *   Người dùng (độc giả, thủ thư, admin) có kết nối internet ổn định để truy cập hệ thống.
    *   Người dùng có kiến thức cơ bản về cách sử dụng trình duyệt web.
    *   Dữ liệu đầu vào từ người dùng được coi là hợp lệ sau khi qua các bước kiểm tra (validation) của hệ thống.
    *   Hạ tầng mạng nội bộ và kết nối giữa các microservice là ổn định và có băng thông đủ.
    *   Có sẵn hạ tầng máy chủ (vật lý hoặc ảo hóa/cloud) để triển khai các microservice, database, Kafka, Redis.
    *   Các dịch vụ bên ngoài (nếu có, như SMS gateway, email server) hoạt động ổn định.
*   **Phụ thuộc:**
    *   Hệ thống phụ thuộc vào sự ổn định và hiệu năng của các thành phần công nghệ nền tảng: Java Virtual Machine (JVM), Spring Boot, PostgreSQL, Redis, Kafka, Eureka.
    *   Việc gửi thông báo qua email phụ thuộc vào cấu hình và hoạt động của SMTP server (có thể là dịch vụ bên ngoài như Gmail SMTP, SendGrid hoặc server nội bộ).
    *   Việc gửi thông báo qua SMS/Push Notification (nếu triển khai) sẽ phụ thuộc vào các nhà cung cấp dịch vụ bên thứ ba.
    *   Quá trình triển khai và vận hành phụ thuộc vào môi trường hạ tầng (phần cứng, mạng, hệ điều hành) được cung cấp.




## 3. Yêu cầu chức năng (Functional Requirements)

Phần này mô tả chi tiết các chức năng mà Hệ thống Thư viện Mượn Sách phải cung cấp. Mỗi chức năng được xác định bởi một ID duy nhất, tên, mô tả, các bước thực hiện, đầu vào/đầu ra dự kiến, cùng với các ràng buộc và cách xử lý lỗi.

### 3.1. Quản lý Người dùng (User Management)

Nhóm chức năng này liên quan đến việc quản lý tài khoản và thông tin của tất cả người dùng hệ thống (Độc giả, Thủ thư, Quản trị viên).

#### FR-USER-001: Đăng ký tài khoản Độc giả
*   **Tên chức năng:** Đăng ký tài khoản Độc giả
*   **Mô tả:** Cho phép người dùng mới (độc giả tiềm năng) tạo một tài khoản trong hệ thống để có thể sử dụng các dịch vụ của thư viện.
*   **Các bước thực hiện:**
    1.  Người dùng truy cập trang đăng ký.
    2.  Người dùng nhập các thông tin bắt buộc: tên đăng nhập (username), địa chỉ email, mật khẩu, xác nhận mật khẩu, họ, tên.
    3.  Người dùng có thể nhập các thông tin tùy chọn: số điện thoại.
    4.  Người dùng nhấn nút "Đăng ký".
    5.  Hệ thống kiểm tra tính hợp lệ của thông tin (định dạng email, mật khẩu đủ mạnh, tên đăng nhập/email chưa tồn tại).
    6.  Nếu thông tin hợp lệ, hệ thống tạo tài khoản mới với vai trò mặc định là "Độc giả", lưu thông tin vào cơ sở dữ liệu (User Service).
    7.  Hệ thống gửi sự kiện `user-created` đến Kafka.
    8.  Hệ thống hiển thị thông báo đăng ký thành công và chuyển hướng người dùng đến trang đăng nhập hoặc trang thông tin cá nhân.
    9.  (Notification Service) Nhận sự kiện `user-created` và gửi email chào mừng đến địa chỉ email đã đăng ký.
*   **Đầu vào:** Thông tin đăng ký của người dùng (username, email, password, first_name, last_name, phone - tùy chọn).
*   **Đầu ra:** Tài khoản người dùng mới được tạo, thông báo thành công, email chào mừng.
*   **Ràng buộc và xử lý lỗi:**
    *   Tên đăng nhập và email phải là duy nhất trong hệ thống. Nếu trùng, hiển thị lỗi.
    *   Mật khẩu phải đáp ứng các tiêu chí về độ phức tạp (ví dụ: ít nhất 8 ký tự, có chữ hoa, chữ thường, số). Nếu không, hiển thị lỗi.
    *   Xác nhận mật khẩu phải khớp với mật khẩu. Nếu không, hiển thị lỗi.
    *   Email phải đúng định dạng. Nếu không, hiển thị lỗi.
    *   Các trường bắt buộc không được để trống. Nếu trống, hiển thị lỗi.
    *   Nếu có lỗi xảy ra trong quá trình lưu dữ liệu hoặc gửi sự kiện Kafka, hệ thống cần ghi log lỗi và hiển thị thông báo lỗi chung cho người dùng.

#### FR-USER-002: Đăng nhập hệ thống
*   **Tên chức năng:** Đăng nhập hệ thống
*   **Mô tả:** Cho phép người dùng đã có tài khoản truy cập vào hệ thống bằng tên đăng nhập (hoặc email) và mật khẩu.
*   **Các bước thực hiện:**
    1.  Người dùng truy cập trang đăng nhập.
    2.  Người dùng nhập tên đăng nhập (hoặc email) và mật khẩu.
    3.  Người dùng nhấn nút "Đăng nhập".
    4.  Hệ thống (API Gateway chuyển đến User Service) xác thực thông tin đăng nhập.
    5.  Nếu thông tin chính xác, User Service tạo một JWT token (bao gồm thông tin người dùng và vai trò) và trả về cho client.
    6.  Client lưu trữ token (ví dụ: trong Local Storage hoặc Session Storage) và sử dụng token này trong header của các request tiếp theo.
    7.  Hệ thống chuyển hướng người dùng đến trang chủ tương ứng với vai trò của họ (trang dashboard độc giả, trang quản lý thủ thư, trang admin).
*   **Đầu vào:** Tên đăng nhập (hoặc email), mật khẩu.
*   **Đầu ra:** JWT token, chuyển hướng đến trang phù hợp.
*   **Ràng buộc và xử lý lỗi:**
    *   Nếu tên đăng nhập/email hoặc mật khẩu không đúng, hiển thị thông báo lỗi "Tên đăng nhập hoặc mật khẩu không chính xác".
    *   Nếu tài khoản bị khóa hoặc chưa kích hoạt (nếu có cơ chế này), hiển thị thông báo lỗi tương ứng.
    *   Xử lý lỗi nếu User Service không phản hồi hoặc có lỗi trong quá trình tạo token.

#### FR-USER-003: Đăng xuất hệ thống
*   **Tên chức năng:** Đăng xuất hệ thống
*   **Mô tả:** Cho phép người dùng đang đăng nhập kết thúc phiên làm việc và ra khỏi hệ thống một cách an toàn.
*   **Các bước thực hiện:**
    1.  Người dùng nhấn vào nút/link "Đăng xuất".
    2.  Client xóa JWT token đã lưu trữ.
    3.  Hệ thống (có thể gọi API logout của User Service để thực hiện blacklist token nếu cần) hủy phiên làm việc.
    4.  Hệ thống chuyển hướng người dùng về trang chủ hoặc trang đăng nhập.
*   **Đầu vào:** Yêu cầu đăng xuất từ người dùng (thông qua việc click).
*   **Đầu ra:** Phiên làm việc kết thúc, người dùng được chuyển hướng.
*   **Ràng buộc và xử lý lỗi:** Xử lý trường hợp người dùng chưa đăng nhập mà cố gắng đăng xuất (thường không xảy ra nếu UI được thiết kế đúng).

#### FR-USER-004: Quản lý thông tin cá nhân
*   **Tên chức năng:** Quản lý thông tin cá nhân
*   **Mô tả:** Cho phép người dùng xem và cập nhật thông tin cá nhân của mình (họ tên, email, số điện thoại, đổi mật khẩu).
*   **Các bước thực hiện:**
    1.  Người dùng truy cập trang quản lý thông tin cá nhân.
    2.  Hệ thống hiển thị thông tin hiện tại của người dùng (lấy từ User Service dựa trên token).
    3.  Người dùng chỉnh sửa các thông tin muốn thay đổi (họ, tên, số điện thoại).
    4.  Để đổi mật khẩu, người dùng nhập mật khẩu cũ, mật khẩu mới và xác nhận mật khẩu mới.
    5.  Người dùng nhấn nút "Lưu thay đổi" hoặc "Đổi mật khẩu".
    6.  Hệ thống (User Service) kiểm tra tính hợp lệ của dữ liệu (ví dụ: mật khẩu cũ đúng, mật khẩu mới đủ mạnh, xác nhận khớp).
    7.  Nếu hợp lệ, hệ thống cập nhật thông tin vào cơ sở dữ liệu.
    8.  Hệ thống gửi sự kiện `user-updated` đến Kafka.
    9.  Hệ thống hiển thị thông báo cập nhật thành công.
*   **Đầu vào:** Thông tin cập nhật (họ, tên, số điện thoại), mật khẩu cũ, mật khẩu mới, xác nhận mật khẩu mới.
*   **Đầu ra:** Thông tin người dùng được cập nhật, thông báo thành công.
*   **Ràng buộc và xử lý lỗi:**
    *   Email không được phép thay đổi (hoặc cần quy trình xác minh phức tạp hơn).
    *   Tên đăng nhập không được phép thay đổi.
    *   Mật khẩu cũ phải đúng.
    *   Mật khẩu mới phải đủ mạnh và khác mật khẩu cũ.
    *   Xác nhận mật khẩu mới phải khớp.
    *   Hiển thị lỗi cụ thể cho từng trường hợp không hợp lệ.
    *   Xử lý lỗi nếu cập nhật thất bại.

#### FR-USER-005: Quản lý Người dùng (Admin/Librarian)
*   **Tên chức năng:** Quản lý Người dùng (Admin/Librarian)
*   **Mô tả:** Cho phép Quản trị viên hoặc Thủ thư (với quyền hạn phù hợp) xem danh sách người dùng, xem chi tiết, cập nhật thông tin (ví dụ: cấp/thu hồi vai trò Thủ thư cho Admin, cập nhật thông tin cơ bản cho Thủ thư), và xóa tài khoản người dùng (chỉ Admin).
*   **Các bước thực hiện:**
    1.  Admin/Librarian truy cập chức năng quản lý người dùng.
    2.  Hệ thống hiển thị danh sách người dùng với các thông tin cơ bản (tên đăng nhập, họ tên, email, vai trò) và các tùy chọn lọc, tìm kiếm, phân trang.
    3.  Admin/Librarian có thể xem chi tiết thông tin của một người dùng.
    4.  Admin/Librarian có thể chỉnh sửa thông tin người dùng (tùy thuộc quyền hạn).
    5.  Admin có thể thay đổi vai trò của người dùng (ví dụ: nâng cấp Độc giả thành Thủ thư, hoặc ngược lại).
    6.  Admin có thể xóa tài khoản người dùng (cần cơ chế xác nhận).
    7.  Khi thông tin được cập nhật hoặc người dùng bị xóa, hệ thống (User Service) cập nhật cơ sở dữ liệu và gửi sự kiện Kafka tương ứng (`user-updated`, `user-deleted`).
*   **Đầu vào:** Tiêu chí tìm kiếm/lọc, thông tin cập nhật, yêu cầu xóa.
*   **Đầu ra:** Danh sách người dùng, thông tin chi tiết người dùng, kết quả cập nhật/xóa.
*   **Ràng buộc và xử lý lỗi:**
    *   Chỉ Admin mới có quyền xóa người dùng và quản lý vai trò Admin/Librarian.
    *   Librarian chỉ có thể xem và cập nhật thông tin cơ bản của Độc giả (nếu được phép).
    *   Không thể xóa tài khoản Admin gốc hoặc tài khoản đang có sách mượn chưa trả (cần kiểm tra ràng buộc với Book Service).
    *   Cần có xác nhận trước khi thực hiện thao tác xóa.
    *   Xử lý lỗi truy cập, lỗi cập nhật/xóa.

#### FR-USER-006: Quản lý Thẻ thư viện
*   **Tên chức năng:** Quản lý Thẻ thư viện
*   **Mô tả:** Cho phép Admin/Librarian tạo mới, xem, cập nhật trạng thái và gia hạn thẻ thư viện cho độc giả. Độc giả có thể xem thông tin thẻ của mình.
*   **Các bước thực hiện (Admin/Librarian):**
    1.  Truy cập chức năng quản lý thẻ thư viện (có thể tích hợp trong quản lý người dùng).
    2.  Tìm kiếm độc giả cần quản lý thẻ.
    3.  Tạo thẻ mới: Nhập thông tin (nếu cần, hệ thống có thể tự sinh số thẻ), thiết lập ngày hết hạn mặc định (ví dụ: 1 năm).
    4.  Xem thông tin thẻ: Số thẻ, ngày cấp, ngày hết hạn, trạng thái (Hoạt động, Hết hạn, Bị khóa).
    5.  Cập nhật trạng thái: Khóa thẻ (nếu độc giả vi phạm quy định), Mở khóa thẻ.
    6.  Gia hạn thẻ: Chọn thẻ và thiết lập ngày hết hạn mới.
    7.  Hệ thống (User Service) lưu thông tin thẻ, cập nhật trạng thái/ngày hết hạn và gửi sự kiện Kafka (`card-created`, `card-updated`, `card-renewed`).
*   **Các bước thực hiện (Độc giả):**
    1.  Truy cập trang thông tin cá nhân.
    2.  Xem thông tin thẻ thư viện của mình (số thẻ, ngày hết hạn, trạng thái).
*   **Đầu vào:** Thông tin độc giả, yêu cầu tạo/cập nhật/gia hạn thẻ.
*   **Đầu ra:** Thông tin thẻ thư viện được tạo/cập nhật, trạng thái thẻ.
*   **Ràng buộc và xử lý lỗi:**
    *   Mỗi độc giả chỉ có một thẻ thư viện hoạt động tại một thời điểm.
    *   Số thẻ phải là duy nhất.
    *   Ngày hết hạn phải là một ngày trong tương lai.
    *   Xử lý lỗi khi tạo/cập nhật/gia hạn thẻ.

### 3.2. Quản lý Sách (Book Management)

Nhóm chức năng này liên quan đến việc quản lý kho sách của thư viện.

#### FR-BOOK-001: Quản lý Thông tin Sách (Admin/Librarian)
*   **Tên chức năng:** Quản lý Thông tin Sách
*   **Mô tả:** Cho phép Admin/Librarian thêm mới, xem, cập nhật và xóa thông tin chi tiết về các đầu sách trong thư viện (tiêu đề, ISBN, mô tả, ảnh bìa, năm xuất bản, liên kết với tác giả, thể loại, nhà xuất bản).
*   **Các bước thực hiện:**
    1.  Admin/Librarian truy cập chức năng quản lý sách.
    2.  Hệ thống hiển thị danh sách các đầu sách với thông tin cơ bản và tùy chọn lọc, tìm kiếm, phân trang.
    3.  Thêm sách mới: Nhập đầy đủ thông tin sách (tiêu đề, ISBN, mô tả...), chọn tác giả, thể loại, nhà xuất bản từ danh sách có sẵn (hoặc thêm mới nếu chưa có - xem FR-BOOK-003, FR-BOOK-004, FR-BOOK-005), tải lên ảnh bìa.
    4.  Xem chi tiết sách: Hiển thị đầy đủ thông tin của một đầu sách.
    5.  Cập nhật sách: Chỉnh sửa thông tin sách đã có.
    6.  Xóa sách: Xóa thông tin một đầu sách (cần xác nhận). Chỉ thực hiện được nếu không còn bản sao nào của sách tồn tại hoặc đang được mượn.
    7.  Hệ thống (Book Service) lưu/cập nhật/xóa thông tin sách trong cơ sở dữ liệu.
*   **Đầu vào:** Thông tin sách mới, thông tin cập nhật, yêu cầu xóa.
*   **Đầu ra:** Danh sách sách, thông tin chi tiết sách, kết quả thêm/sửa/xóa.
*   **Ràng buộc và xử lý lỗi:**
    *   ISBN (nếu có) phải là duy nhất.
    *   Tiêu đề không được để trống.
    *   Phải chọn ít nhất một tác giả và một thể loại.
    *   Không thể xóa sách nếu vẫn còn bản sao (book copies) liên kết với nó.
    *   Xử lý lỗi nhập liệu, lỗi lưu trữ, lỗi tải ảnh.

#### FR-BOOK-002: Quản lý Bản sao Sách (Admin/Librarian)
*   **Tên chức năng:** Quản lý Bản sao Sách
*   **Mô tả:** Cho phép Admin/Librarian quản lý các bản sao cụ thể của từng đầu sách, bao gồm thêm mới, cập nhật trạng thái (Có sẵn, Đang mượn, Đặt trước, Bảo trì), tình trạng vật lý, vị trí trên kệ.
*   **Các bước thực hiện:**
    1.  Trong trang chi tiết của một đầu sách, Admin/Librarian truy cập mục quản lý bản sao.
    2.  Hệ thống hiển thị danh sách các bản sao của đầu sách đó (số bản sao, trạng thái, vị trí...). 
    3.  Thêm bản sao mới: Nhập số bản sao (copy number - phải là duy nhất cho đầu sách đó), chọn tình trạng, vị trí.\n    4.  Cập nhật bản sao: Thay đổi trạng thái (ví dụ: chuyển sang "Bảo trì"), tình trạng, vị trí.
    5.  Xóa bản sao: Xóa một bản sao cụ thể (chỉ khi trạng thái là "Có sẵn" và không có đặt trước nào liên quan).
    6.  Hệ thống (Book Service) lưu/cập nhật/xóa thông tin bản sao.
*   **Đầu vào:** Thông tin bản sao mới (số bản sao, tình trạng, vị trí), thông tin cập nhật, yêu cầu xóa.
*   **Đầu ra:** Danh sách bản sao, kết quả thêm/sửa/xóa.
*   **Ràng buộc và xử lý lỗi:**
    *   Số bản sao phải là duy nhất cho mỗi đầu sách.
    *   Không thể xóa bản sao đang được mượn hoặc đặt trước.
    *   Trạng thái "Đang mượn", "Đặt trước" thường được cập nhật tự động bởi chức năng mượn/trả/đặt trước, không nên cho phép cập nhật thủ công (trừ trường hợp đặc biệt bởi Admin).
    *   Xử lý lỗi nhập liệu, lỗi lưu trữ.

#### FR-BOOK-003: Quản lý Tác giả (Admin/Librarian)
*   **Tên chức năng:** Quản lý Tác giả
*   **Mô tả:** Cho phép Admin/Librarian thêm, sửa, xóa thông tin tác giả.
*   **Các bước thực hiện:**
    1.  Truy cập chức năng quản lý tác giả.
    2.  Hệ thống hiển thị danh sách tác giả.
    3.  Thêm tác giả mới: Nhập tên tác giả, tiểu sử (tùy chọn).
    4.  Sửa thông tin tác giả.
    5.  Xóa tác giả (chỉ khi không còn sách nào liên kết với tác giả này).
    6.  Hệ thống (Book Service) lưu/cập nhật/xóa thông tin.
*   **Đầu vào:** Tên tác giả, tiểu sử, thông tin cập nhật, yêu cầu xóa.
*   **Đầu ra:** Danh sách tác giả, kết quả thêm/sửa/xóa.
*   **Ràng buộc và xử lý lỗi:** Tên tác giả không được trống. Không xóa được tác giả nếu có sách liên kết.

#### FR-BOOK-004: Quản lý Thể loại (Admin/Librarian)
*   **Tên chức năng:** Quản lý Thể loại
*   **Mô tả:** Cho phép Admin/Librarian thêm, sửa, xóa thông tin thể loại sách.
*   **Các bước thực hiện:** Tương tự quản lý tác giả (FR-BOOK-003), thay "tác giả" bằng "thể loại".
*   **Đầu vào:** Tên thể loại, mô tả, thông tin cập nhật, yêu cầu xóa.
*   **Đầu ra:** Danh sách thể loại, kết quả thêm/sửa/xóa.
*   **Ràng buộc và xử lý lỗi:** Tên thể loại phải duy nhất và không được trống. Không xóa được thể loại nếu có sách liên kết.

#### FR-BOOK-005: Quản lý Nhà xuất bản (Admin/Librarian)
*   **Tên chức năng:** Quản lý Nhà xuất bản
*   **Mô tả:** Cho phép Admin/Librarian thêm, sửa, xóa thông tin nhà xuất bản.
*   **Các bước thực hiện:** Tương tự quản lý tác giả (FR-BOOK-003), thay "tác giả" bằng "nhà xuất bản".
*   **Đầu vào:** Tên nhà xuất bản, địa chỉ, thông tin cập nhật, yêu cầu xóa.
*   **Đầu ra:** Danh sách nhà xuất bản, kết quả thêm/sửa/xóa.
*   **Ràng buộc và xử lý lỗi:** Tên nhà xuất bản không được trống. Không xóa được nhà xuất bản nếu có sách liên kết.

#### FR-BOOK-006: Tìm kiếm Sách (Độc giả/Librarian/Admin)
*   **Tên chức năng:** Tìm kiếm Sách
*   **Mô tả:** Cho phép người dùng tìm kiếm sách trong thư viện theo nhiều tiêu chí khác nhau.
*   **Các bước thực hiện:**
    1.  Người dùng nhập từ khóa tìm kiếm vào ô tìm kiếm (ví dụ: tiêu đề, tên tác giả, ISBN).
    2.  Người dùng có thể sử dụng bộ lọc nâng cao (thể loại, nhà xuất bản, năm xuất bản).
    3.  Người dùng nhấn nút "Tìm kiếm".
    4.  Hệ thống (Book Service) truy vấn cơ sở dữ liệu dựa trên từ khóa và bộ lọc.
    5.  Hệ thống hiển thị danh sách kết quả sách phù hợp, kèm theo thông tin tóm tắt (ảnh bìa, tiêu đề, tác giả) và trạng thái sẵn có (số bản sao có sẵn).
    6.  Hệ thống hỗ trợ phân trang cho kết quả tìm kiếm.
*   **Đầu vào:** Từ khóa tìm kiếm, tiêu chí lọc.
*   **Đầu ra:** Danh sách sách phù hợp với tiêu chí tìm kiếm.
*   **Ràng buộc và xử lý lỗi:**
    *   Xử lý trường hợp không tìm thấy kết quả nào.
    *   Tối ưu hóa truy vấn tìm kiếm để đảm bảo hiệu suất (có thể sử dụng full-text search của PostgreSQL hoặc tích hợp Elasticsearch).
    *   Xử lý lỗi nếu truy vấn thất bại.

#### FR-BOOK-007: Xem Chi tiết Sách (Độc giả/Librarian/Admin)
*   **Tên chức năng:** Xem Chi tiết Sách
*   **Mô tả:** Cho phép người dùng xem thông tin chi tiết về một đầu sách cụ thể.
*   **Các bước thực hiện:**
    1.  Người dùng chọn một cuốn sách từ kết quả tìm kiếm hoặc danh sách khác.
    2.  Hệ thống (Book Service) lấy thông tin chi tiết của sách (tiêu đề, ảnh bìa, tác giả, thể loại, nhà xuất bản, năm xuất bản, mô tả, ISBN).
    3.  Hệ thống hiển thị thông tin chi tiết sách.
    4.  Hệ thống hiển thị danh sách các bản sao của sách cùng trạng thái (Có sẵn, Đang mượn, Hạn trả dự kiến nếu đang mượn, Đặt trước) và vị trí.
    5.  Nếu là độc giả và sách có bản sao đang được mượn, hiển thị nút "Đặt trước" (xem FR-RSV-001).
    6.  Nếu là độc giả và sách có bản sao "Có sẵn", hiển thị nút "Mượn sách" (hoặc thông tin để mượn tại quầy).
*   **Đầu vào:** ID của sách cần xem.
*   **Đầu ra:** Trang chi tiết thông tin sách và các bản sao.
*   **Ràng buộc và xử lý lỗi:** Xử lý trường hợp ID sách không tồn tại. Lỗi khi lấy thông tin sách hoặc bản sao.

*(Tiếp tục với các nhóm chức năng khác: Mượn/Trả, Đặt trước, Thông báo...)*




### 3.3. Quản lý Mượn/Trả sách (Borrowing/Returning Management)

Nhóm chức năng này xử lý quy trình mượn và trả sách của độc giả.

#### FR-BRW-001: Mượn Sách
*   **Tên chức năng:** Mượn Sách
*   **Mô tả:** Cho phép Thủ thư ghi nhận việc độc giả mượn một bản sao sách cụ thể. (Lưu ý: Quy trình có thể thay đổi nếu cho phép độc giả tự mượn qua hệ thống).
*   **Các bước thực hiện (Quy trình Thủ thư xử lý):**
    1.  Thủ thư truy cập chức năng mượn sách.
    2.  Thủ thư quét mã vạch thẻ thư viện của độc giả hoặc tìm kiếm độc giả bằng tên/mã số.
    3.  Hệ thống (Book Service gọi User Service) kiểm tra trạng thái thẻ thư viện và giới hạn mượn sách của độc giả (ví dụ: thẻ còn hạn, chưa mượn quá số lượng cho phép).
    4.  Nếu đủ điều kiện, Thủ thư quét mã vạch của bản sao sách hoặc nhập số bản sao.
    5.  Hệ thống (Book Service) kiểm tra trạng thái của bản sao sách (phải là "Có sẵn").
    6.  Nếu sách có sẵn, hệ thống ghi nhận giao dịch mượn: liên kết bản sao sách với độc giả, đặt ngày mượn là ngày hiện tại, tính toán và đặt ngày trả dự kiến (dựa trên quy định của thư viện), cập nhật trạng thái bản sao sách thành "Đang mượn".
    7.  Hệ thống (Book Service) lưu thông tin giao dịch mượn vào cơ sở dữ liệu.
    8.  Hệ thống gửi sự kiện `book-borrowed` đến Kafka.
    9.  Hệ thống hiển thị thông báo mượn sách thành công, bao gồm thông tin sách và hạn trả.
    10. (Notification Service) Nhận sự kiện `book-borrowed` và gửi thông báo xác nhận mượn sách cho độc giả.
*   **Đầu vào:** ID Độc giả (hoặc thông tin tìm kiếm), ID Bản sao sách (hoặc số bản sao).
*   **Đầu ra:** Giao dịch mượn sách được ghi nhận, trạng thái bản sao sách cập nhật, thông báo thành công, thông báo xác nhận cho độc giả.
*   **Ràng buộc và xử lý lỗi:**
    *   Thẻ thư viện phải hợp lệ (còn hạn, không bị khóa).
    *   Độc giả không được mượn vượt quá số lượng sách quy định.
    *   Bản sao sách phải ở trạng thái "Có sẵn".
    *   Nếu không đủ điều kiện mượn hoặc sách không có sẵn, hiển thị thông báo lỗi rõ ràng.
    *   Xử lý lỗi nếu không thể kiểm tra thông tin độc giả từ User Service.
    *   Xử lý lỗi nếu lưu giao dịch mượn hoặc gửi sự kiện Kafka thất bại.

#### FR-BRW-002: Trả Sách
*   **Tên chức năng:** Trả Sách
*   **Mô tả:** Cho phép Thủ thư ghi nhận việc độc giả trả lại một bản sao sách đã mượn.
*   **Các bước thực hiện (Quy trình Thủ thư xử lý):**
    1.  Thủ thư truy cập chức năng trả sách.
    2.  Thủ thư quét mã vạch của bản sao sách hoặc nhập số bản sao.
    3.  Hệ thống (Book Service) tìm giao dịch mượn đang hoạt động (status = ACTIVE) cho bản sao sách này.
    4.  Nếu tìm thấy giao dịch, hệ thống ghi nhận việc trả sách: đặt ngày trả là ngày hiện tại, cập nhật trạng thái giao dịch mượn thành "Đã trả" (RETURNED).
    5.  Hệ thống kiểm tra xem sách có bị trả muộn hay không (so sánh ngày trả với ngày hạn trả).
    6.  Nếu trả muộn, hệ thống tính toán phí phạt dựa trên quy định của thư viện và cập nhật vào giao dịch mượn.
    7.  Hệ thống cập nhật trạng thái của bản sao sách. Nếu không có ai đặt trước sách này, trạng thái trở thành "Có sẵn". Nếu có đặt trước, trạng thái trở thành "Đặt trước" (RESERVED) và kích hoạt quy trình thông báo cho người đặt trước (xem FR-RSV-005).
    8.  Hệ thống (Book Service) lưu các thay đổi vào cơ sở dữ liệu.
    9.  Hệ thống gửi sự kiện `book-returned` (và có thể cả `reservation-available` nếu có đặt trước) đến Kafka.
    10. Hệ thống hiển thị thông báo trả sách thành công, kèm thông tin về phí phạt (nếu có).
    11. (Notification Service) Nhận sự kiện `book-returned` và gửi thông báo xác nhận trả sách (kèm phí phạt nếu có) cho độc giả.
*   **Đầu vào:** ID Bản sao sách (hoặc số bản sao).
*   **Đầu ra:** Giao dịch mượn được cập nhật, trạng thái bản sao sách cập nhật, thông tin phí phạt (nếu có), thông báo thành công, thông báo xác nhận cho độc giả.
*   **Ràng buộc và xử lý lỗi:**
    *   Bản sao sách phải đang ở trạng thái "Đang mượn". Nếu không, hiển thị lỗi.
    *   Phải tìm thấy giao dịch mượn đang hoạt động tương ứng. Nếu không, hiển thị lỗi.
    *   Quy trình tính phí phạt phải chính xác theo quy định.
    *   Xử lý lỗi nếu cập nhật cơ sở dữ liệu hoặc gửi sự kiện Kafka thất bại.

#### FR-BRW-003: Xem Lịch sử Mượn/Trả
*   **Tên chức năng:** Xem Lịch sử Mượn/Trả
*   **Mô tả:** Cho phép Độc giả xem lịch sử mượn/trả sách của chính mình. Cho phép Thủ thư/Admin xem lịch sử mượn/trả của một độc giả cụ thể hoặc toàn bộ hệ thống.
*   **Các bước thực hiện (Độc giả):**
    1.  Độc giả truy cập trang lịch sử mượn/trả trong tài khoản cá nhân.
    2.  Hệ thống (Book Service) truy vấn và hiển thị danh sách các giao dịch mượn đã hoàn thành và đang hoạt động của độc giả (tên sách, ngày mượn, ngày hạn trả, ngày trả thực tế, phí phạt nếu có).
    3.  Hỗ trợ lọc/sắp xếp lịch sử.
*   **Các bước thực hiện (Thủ thư/Admin):**
    1.  Truy cập chức năng quản lý lịch sử mượn/trả.
    2.  Tìm kiếm độc giả (nếu cần).
    3.  Hệ thống hiển thị lịch sử mượn/trả tương tự như độc giả, có thể có thêm thông tin quản lý.
    4.  Hỗ trợ xem lịch sử theo sách, theo độc giả, theo khoảng thời gian.
*   **Đầu vào:** ID Độc giả (tự động lấy từ token cho độc giả, nhập/tìm kiếm cho Thủ thư/Admin), tiêu chí lọc/sắp xếp.
*   **Đầu ra:** Danh sách lịch sử các giao dịch mượn/trả.
*   **Ràng buộc và xử lý lỗi:** Xử lý lỗi khi truy vấn dữ liệu.

#### FR-BRW-004: Quản lý Sách Quá hạn và Phí phạt
*   **Tên chức năng:** Quản lý Sách Quá hạn và Phí phạt
*   **Mô tả:** Cho phép Thủ thư/Admin xem danh sách các sách đang bị mượn quá hạn, xem thông tin phí phạt và ghi nhận việc thanh toán phí phạt.
*   **Các bước thực hiện:**
    1.  Thủ thư/Admin truy cập chức năng quản lý sách quá hạn.
    2.  Hệ thống (Book Service) truy vấn và hiển thị danh sách các giao dịch mượn đang có trạng thái "Quá hạn" (OVERDUE) hoặc các giao dịch "Đã trả" nhưng có phí phạt chưa thanh toán.
    3.  Hiển thị thông tin chi tiết: tên sách, tên độc giả, ngày mượn, ngày hạn trả, số ngày quá hạn, số tiền phạt.
    4.  Thủ thư/Admin có thể ghi nhận việc độc giả đã thanh toán phí phạt cho một giao dịch cụ thể.
    5.  Hệ thống cập nhật trạng thái thanh toán phí phạt trong cơ sở dữ liệu.
*   **Đầu vào:** Yêu cầu xem danh sách quá hạn, ID giao dịch cần ghi nhận thanh toán.
*   **Đầu ra:** Danh sách sách quá hạn và phí phạt, kết quả ghi nhận thanh toán.
*   **Ràng buộc và xử lý lỗi:**
    *   Quy trình tính phí phạt phải được định nghĩa rõ ràng.
    *   Cần có cơ chế xác nhận khi ghi nhận thanh toán.
    *   Xử lý lỗi khi truy vấn hoặc cập nhật dữ liệu.

### 3.4. Quản lý Đặt trước (Reservation Management)

Nhóm chức năng này cho phép độc giả đặt trước những cuốn sách hiện không có sẵn.

#### FR-RSV-001: Đặt trước Sách
*   **Tên chức năng:** Đặt trước Sách
*   **Mô tả:** Cho phép Độc giả đặt trước một đầu sách hiện đang được mượn hết.
*   **Các bước thực hiện:**
    1.  Độc giả xem trang chi tiết của một đầu sách (FR-BOOK-007).
    2.  Nếu tất cả các bản sao của sách đang ở trạng thái "Đang mượn", nút "Đặt trước" sẽ hiển thị.
    3.  Độc giả nhấn nút "Đặt trước".
    4.  Hệ thống (Book Service) kiểm tra xem độc giả có đủ điều kiện đặt trước không (ví dụ: không có sách quá hạn chưa trả, chưa đạt giới hạn đặt trước tối đa).
    5.  Nếu đủ điều kiện, hệ thống tạo một yêu cầu đặt trước cho đầu sách này, liên kết với độc giả, ghi nhận ngày đặt trước và tính ngày hết hạn đặt trước (ví dụ: 7 ngày kể từ khi sách có sẵn).
    6.  Hệ thống lưu thông tin đặt trước vào cơ sở dữ liệu.
    7.  Hệ thống gửi sự kiện `book-reserved` đến Kafka.
    8.  Hệ thống hiển thị thông báo đặt trước thành công.
    9.  (Notification Service) Nhận sự kiện `book-reserved` và gửi thông báo xác nhận đặt trước cho độc giả.
*   **Đầu vào:** ID Đầu sách, ID Độc giả (từ token).
*   **Đầu ra:** Yêu cầu đặt trước được tạo, thông báo thành công, thông báo xác nhận cho độc giả.
*   **Ràng buộc và xử lý lỗi:**
    *   Chỉ có thể đặt trước sách khi không còn bản sao nào "Có sẵn".
    *   Độc giả phải đáp ứng các điều kiện đặt trước (nếu có).
    *   Không thể đặt trước sách mà chính mình đang mượn.
    *   Một độc giả không thể đặt trước cùng một đầu sách nhiều lần.
    *   Xử lý lỗi nếu không đủ điều kiện hoặc lưu đặt trước thất bại.

#### FR-RSV-002: Hủy Đặt trước
*   **Tên chức năng:** Hủy Đặt trước
*   **Mô tả:** Cho phép Độc giả hủy yêu cầu đặt trước sách của mình.
*   **Các bước thực hiện:**
    1.  Độc giả truy cập danh sách các sách đã đặt trước của mình (xem FR-RSV-003).
    2.  Độc giả chọn yêu cầu đặt trước muốn hủy và nhấn nút "Hủy".
    3.  Hệ thống (Book Service) cập nhật trạng thái của yêu cầu đặt trước thành "Đã hủy" (CANCELLED).
    4.  Hệ thống lưu thay đổi.
    5.  Hệ thống có thể gửi sự kiện `reservation-cancelled` đến Kafka (tùy chọn).
    6.  Hệ thống hiển thị thông báo hủy thành công.
    7.  (Notification Service) Có thể gửi thông báo xác nhận hủy (tùy chọn).
*   **Đầu vào:** ID Yêu cầu đặt trước cần hủy.
*   **Đầu ra:** Yêu cầu đặt trước được cập nhật trạng thái, thông báo thành công.
*   **Ràng buộc và xử lý lỗi:** Chỉ có thể hủy các yêu cầu đặt trước đang ở trạng thái "Đang chờ" (PENDING). Xử lý lỗi nếu cập nhật thất bại.

#### FR-RSV-003: Xem Danh sách Đặt trước
*   **Tên chức năng:** Xem Danh sách Đặt trước
*   **Mô tả:** Cho phép Độc giả xem danh sách các sách mình đã đặt trước. Cho phép Thủ thư/Admin xem tất cả các yêu cầu đặt trước hoặc theo từng sách/độc giả.
*   **Các bước thực hiện (Độc giả):**
    1.  Độc giả truy cập trang quản lý đặt trước trong tài khoản cá nhân.
    2.  Hệ thống (Book Service) hiển thị danh sách các yêu cầu đặt trước đang chờ (PENDING) hoặc đã có sách (FULFILLED - đang chờ lấy) của độc giả, bao gồm tên sách, ngày đặt, trạng thái, vị trí trong hàng đợi (nếu có).
*   **Các bước thực hiện (Thủ thư/Admin):**
    1.  Truy cập chức năng quản lý đặt trước.
    2.  Hệ thống hiển thị danh sách tất cả các yêu cầu đặt trước, có thể lọc theo sách, độc giả, trạng thái.
*   **Đầu vào:** ID Độc giả (tự động), tiêu chí lọc.
*   **Đầu ra:** Danh sách các yêu cầu đặt trước.
*   **Ràng buộc và xử lý lỗi:** Xử lý lỗi khi truy vấn dữ liệu.

#### FR-RSV-004: Quản lý Hàng đợi Đặt trước
*   **Tên chức năng:** Quản lý Hàng đợi Đặt trước
*   **Mô tả:** Hệ thống tự động quản lý hàng đợi đặt trước cho mỗi đầu sách theo thứ tự thời gian đặt.
*   **Các bước thực hiện:**
    1.  Khi một bản sao sách được trả (FR-BRW-002) và có yêu cầu đặt trước đang chờ (PENDING) cho đầu sách đó, hệ thống xác định yêu cầu đặt trước sớm nhất trong hàng đợi.
    2.  Hệ thống cập nhật trạng thái của yêu cầu đặt trước đó thành "Có sẵn để lấy" (FULFILLED).
    3.  Hệ thống cập nhật trạng thái của bản sao sách vừa trả thành "Đặt trước" (RESERVED) và liên kết tạm thời với yêu cầu đặt trước được chọn.
    4.  Hệ thống tính toán ngày hết hạn lấy sách (ví dụ: 3 ngày kể từ ngày thông báo).
    5.  Hệ thống gửi sự kiện `reservation-available` đến Kafka.
    6.  (Notification Service) Nhận sự kiện và gửi thông báo cho độc giả biết sách đã có sẵn để lấy.
*   **Đầu vào:** Sự kiện trả sách có đặt trước.
*   **Đầu ra:** Yêu cầu đặt trước và bản sao sách được cập nhật trạng thái, sự kiện `reservation-available` được gửi.
*   **Ràng buộc và xử lý lỗi:**
    *   Hàng đợi phải được xử lý đúng thứ tự FIFO (First-In, First-Out).
    *   Cần xử lý trường hợp độc giả không đến lấy sách trước ngày hết hạn (ví dụ: tự động hủy đặt trước và thông báo cho người tiếp theo trong hàng đợi).
    *   Xử lý lỗi trong quá trình cập nhật trạng thái hoặc gửi sự kiện.

#### FR-RSV-005: Hoàn tất Đặt trước (Xử lý tại quầy)
*   **Tên chức năng:** Hoàn tất Đặt trước
*   **Mô tả:** Cho phép Thủ thư ghi nhận việc độc giả đến nhận sách đã đặt trước.
*   **Các bước thực hiện:**
    1.  Độc giả đến quầy thông báo lấy sách đặt trước.
    2.  Thủ thư tìm yêu cầu đặt trước của độc giả (trạng thái FULFILLED).
    3.  Thủ thư xác nhận thông tin độc giả và sách.
    4.  Thủ thư thực hiện quy trình tương tự như Mượn Sách (FR-BRW-001) cho bản sao sách đang được giữ (trạng thái RESERVED).
    5.  Hệ thống cập nhật trạng thái yêu cầu đặt trước thành "Đã hoàn tất" (COMPLETED).
*   **Đầu vào:** ID Yêu cầu đặt trước, ID Độc giả, ID Bản sao sách.
*   **Đầu ra:** Giao dịch mượn sách mới được tạo, yêu cầu đặt trước được hoàn tất.
*   **Ràng buộc và xử lý lỗi:**
    *   Chỉ xử lý các yêu cầu đặt trước có trạng thái FULFILLED và chưa hết hạn lấy sách.
    *   Áp dụng các ràng buộc của chức năng Mượn Sách.

### 3.5. Hệ thống Thông báo (Notification System)

Nhóm chức năng này mô tả các loại thông báo tự động mà hệ thống cần gửi.

#### FR-NTF-001 đến FR-NTF-007: Gửi các loại Thông báo cụ thể
*   **Tên chức năng:** Gửi Thông báo [Loại thông báo]
*   **Mô tả:** Hệ thống (Notification Service) tự động gửi các thông báo sau đến người dùng (chủ yếu qua email, có thể mở rộng SMS/Push) khi các sự kiện tương ứng xảy ra:
    *   **FR-NTF-001:** Xác nhận Mượn sách (khi sự kiện `book-borrowed` được nhận).
    *   **FR-NTF-002:** Xác nhận Trả sách (khi sự kiện `book-returned` được nhận, kèm phí phạt nếu có).
    *   **FR-NTF-003:** Nhắc nhở Hạn trả sách (gửi trước hạn trả một khoảng thời gian nhất định, ví dụ 3 ngày, dựa trên việc quét định kỳ các giao dịch mượn).
    *   **FR-NTF-004:** Thông báo Sách Quá hạn (khi sự kiện `book-overdue` được nhận hoặc quét định kỳ, kèm thông tin phí phạt).
    *   **FR-NTF-005:** Thông báo Sách Đặt trước Có sẵn (khi sự kiện `reservation-available` được nhận, kèm hạn chót lấy sách).
    *   **FR-NTF-006:** Xác nhận Đặt trước/Hủy Đặt trước (khi sự kiện `book-reserved` hoặc `reservation-cancelled` được nhận).
    *   **FR-NTF-007:** Nhắc nhở Thẻ thư viện sắp hết hạn (gửi trước ngày hết hạn, dựa trên quét định kỳ).
*   **Các bước thực hiện:**
    1.  Notification Service nhận sự kiện từ Kafka hoặc thực hiện quét dữ liệu định kỳ.
    2.  Xác định người dùng cần nhận thông báo và loại thông báo.
    3.  Kiểm tra tùy chọn nhận thông báo của người dùng (FR-NTF-008).
    4.  Lấy mẫu thông báo tương ứng (FR-NTF-009).
    5.  Điền thông tin cụ thể vào mẫu (tên người dùng, tên sách, ngày tháng...).
    6.  Gửi thông báo qua kênh đã cấu hình (ví dụ: email qua Spring Mail).
    7.  Lưu lại lịch sử thông báo vào cơ sở dữ liệu (bảng notifications).
*   **Đầu vào:** Sự kiện Kafka, dữ liệu từ quét định kỳ.
*   **Đầu ra:** Thông báo được gửi đến người dùng, lịch sử thông báo được lưu.
*   **Ràng buộc và xử lý lỗi:**
    *   Thông tin trong thông báo phải chính xác.
    *   Xử lý lỗi khi gửi thông báo (ví dụ: email không hợp lệ, lỗi kết nối SMTP server).
    *   Đảm bảo không gửi thông báo trùng lặp.
    *   Việc quét định kỳ cần được cấu hình hợp lý để không ảnh hưởng hiệu suất.

#### FR-NTF-008: Quản lý Tùy chọn Thông báo
*   **Tên chức năng:** Quản lý Tùy chọn Thông báo
*   **Mô tả:** Cho phép Độc giả tùy chỉnh loại thông báo họ muốn nhận và qua kênh nào (email, SMS, push - nếu hỗ trợ).
*   **Các bước thực hiện:**
    1.  Độc giả truy cập trang cài đặt thông báo trong tài khoản cá nhân.
    2.  Hệ thống hiển thị các loại thông báo (nhắc hạn trả, báo quá hạn, đặt trước...) và các kênh khả dụng.
    3.  Độc giả bật/tắt các tùy chọn theo ý muốn.
    4.  Người dùng nhấn "Lưu thay đổi".
    5.  Hệ thống (Notification Service) lưu các tùy chọn vào cơ sở dữ liệu (bảng notification_preferences).
*   **Đầu vào:** Lựa chọn bật/tắt của độc giả.
*   **Đầu ra:** Tùy chọn thông báo được lưu.
*   **Ràng buộc và xử lý lỗi:** Xử lý lỗi khi lưu tùy chọn.

#### FR-NTF-009: Quản lý Mẫu Thông báo (Admin)
*   **Tên chức năng:** Quản lý Mẫu Thông báo
*   **Mô tả:** Cho phép Quản trị viên xem, chỉnh sửa nội dung và tiêu đề của các mẫu thông báo được hệ thống sử dụng.
*   **Các bước thực hiện:**
    1.  Admin truy cập chức năng quản lý mẫu thông báo.
    2.  Hệ thống hiển thị danh sách các mẫu thông báo hiện có.
    3.  Admin chọn một mẫu để xem/chỉnh sửa.
    4.  Admin chỉnh sửa tiêu đề, nội dung (sử dụng các biến placeholder được định nghĩa trước, ví dụ: `{userName}`, `{bookTitle}`, `{dueDate}`).
    5.  Admin lưu thay đổi.
    6.  Hệ thống (Notification Service) cập nhật mẫu trong cơ sở dữ liệu (bảng notification_templates).
*   **Đầu vào:** Nội dung mẫu thông báo đã chỉnh sửa.
*   **Đầu ra:** Mẫu thông báo được cập nhật.
*   **Ràng buộc và xử lý lỗi:**
    *   Cần có trình soạn thảo hỗ trợ định dạng cơ bản (ví dụ: HTML cho email).
    *   Các biến placeholder phải được giữ nguyên.
    *   Xử lý lỗi khi lưu mẫu.

### 3.6. Quản trị Hệ thống (System Administration)

Nhóm chức năng dành riêng cho Quản trị viên để cấu hình và giám sát hệ thống.

#### FR-ADM-001: Cấu hình Quy định Thư viện
*   **Tên chức năng:** Cấu hình Quy định Thư viện
*   **Mô tả:** Cho phép Admin cấu hình các tham số hoạt động của thư viện.
*   **Các bước thực hiện:**
    1.  Admin truy cập trang cấu hình hệ thống.
    2.  Admin thiết lập các giá trị: số lượng sách tối đa được mượn cùng lúc, thời gian mượn mặc định (theo ngày), tỷ lệ tính phí phạt quá hạn (theo ngày), thời gian giữ sách đặt trước (theo ngày), thời hạn thẻ thư viện mặc định (theo năm).
    3.  Admin lưu cấu hình.
    4.  Hệ thống lưu các giá trị cấu hình này (có thể lưu trong database hoặc file cấu hình được quản lý bởi Spring Cloud Config).
*   **Đầu vào:** Các giá trị cấu hình.
*   **Đầu ra:** Cấu hình hệ thống được cập nhật.
*   **Ràng buộc và xử lý lỗi:** Các giá trị phải là số dương hợp lệ. Xử lý lỗi khi lưu cấu hình.

#### FR-ADM-002: Xem Báo cáo và Thống kê
*   **Tên chức năng:** Xem Báo cáo và Thống kê
*   **Mô tả:** Cung cấp cho Admin các báo cáo và thống kê về hoạt động của thư viện.
*   **Các bước thực hiện:**
    1.  Admin truy cập mục báo cáo.
    2.  Admin chọn loại báo cáo muốn xem (ví dụ: số lượt mượn theo thời gian, sách được mượn nhiều nhất, độc giả mượn nhiều nhất, số sách quá hạn, thống kê người dùng mới...).
    3.  Hệ thống truy vấn dữ liệu từ các service liên quan (chủ yếu là Book Service và User Service) và tổng hợp kết quả.
    4.  Hệ thống hiển thị báo cáo dưới dạng bảng hoặc biểu đồ.
    5.  Hỗ trợ xuất báo cáo ra file (ví dụ: CSV, Excel).
*   **Đầu vào:** Loại báo cáo, khoảng thời gian (nếu có).
*   **Đầu ra:** Báo cáo/thống kê dưới dạng bảng/biểu đồ, file xuất báo cáo.
*   **Ràng buộc và xử lý lỗi:**
    *   Truy vấn báo cáo không được ảnh hưởng lớn đến hiệu năng hoạt động của hệ thống (có thể sử dụng cơ sở dữ liệu replica cho báo cáo).
    *   Xử lý lỗi khi tổng hợp hoặc hiển thị dữ liệu.




## 4. Yêu cầu phi chức năng (Non-Functional Requirements)

Phần này mô tả các yêu cầu không liên quan trực tiếp đến chức năng cụ thể của hệ thống nhưng lại rất quan trọng đối với chất lượng, hiệu quả hoạt động và trải nghiệm người dùng. Các yêu cầu này bao gồm hiệu suất, bảo mật, khả năng mở rộng, tính sẵn sàng, khả năng sử dụng, khả năng bảo trì và các khía cạnh khác.

### 4.1. Hiệu suất (Performance Requirements)

Hiệu suất hệ thống là yếu tố then chốt để đảm bảo trải nghiệm người dùng mượt mà và khả năng đáp ứng của hệ thống dưới tải trọng khác nhau.

*   **NFR-PERF-001: Thời gian phản hồi (Response Time):**
    *   Đối với các thao tác người dùng thông thường trên giao diện web (ví dụ: xem danh sách sách, xem chi tiết sách, xem thông tin cá nhân), thời gian phản hồi từ hệ thống (tính từ lúc người dùng thực hiện hành động đến khi giao diện cập nhật hoàn tất) phải dưới 2 giây trong điều kiện tải bình thường (95% số request).
    *   Đối với các thao tác tìm kiếm sách, thời gian phản hồi phải dưới 3 giây cho các truy vấn phổ biến.
    *   Đối với các thao tác cập nhật dữ liệu (ví dụ: mượn sách, trả sách, cập nhật thông tin), thời gian xử lý phía backend (không tính độ trễ mạng) phải dưới 1 giây.
*   **NFR-PERF-002: Khả năng chịu tải (Throughput):**
    *   Hệ thống phải có khả năng xử lý đồng thời ít nhất 100 yêu cầu đọc (ví dụ: xem sách, tìm kiếm) mỗi giây mà không làm giảm đáng kể thời gian phản hồi.
    *   Hệ thống phải có khả năng xử lý đồng thời ít nhất 20 yêu cầu ghi (ví dụ: mượn, trả, cập nhật) mỗi giây.
    *   Các chỉ số này cần được kiểm tra và xác minh thông qua kiểm thử tải (load testing) trước khi triển khai chính thức.
*   **NFR-PERF-003: Sử dụng tài nguyên (Resource Utilization):**
    *   Trong điều kiện tải bình thường, mức sử dụng CPU của mỗi instance microservice không nên vượt quá 70%.
    *   Mức sử dụng bộ nhớ (RAM) của mỗi instance không nên vượt quá 80% dung lượng được cấp phát.
    *   Việc sử dụng tài nguyên cần được giám sát liên tục để có kế hoạch nâng cấp hoặc tối ưu hóa kịp thời.
*   **NFR-PERF-004: Tối ưu hóa Cache:**
    *   Hệ thống phải tận dụng hiệu quả Redis cache để giảm thời gian truy cập dữ liệu thường xuyên (thông tin sách phổ biến, thông tin người dùng, danh mục, tác giả). Chiến lược cache (thời gian hết hạn, cơ chế cập nhật/invalidate) cần được thiết kế và cấu hình cẩn thận.

### 4.2. Bảo mật (Security Requirements)

Bảo mật là yêu cầu tối quan trọng để bảo vệ dữ liệu người dùng, thông tin sách và đảm bảo tính toàn vẹn của hệ thống.

*   **NFR-SEC-001: Xác thực (Authentication):**
    *   Tất cả các truy cập vào chức năng yêu cầu đăng nhập phải được xác thực bằng JWT token hợp lệ. API Gateway chịu trách nhiệm kiểm tra token trước khi chuyển tiếp yêu cầu đến các microservice.
    *   Mật khẩu người dùng phải được lưu trữ trong cơ sở dữ liệu dưới dạng băm (hashed) sử dụng thuật toán mạnh và an toàn (ví dụ: bcrypt) cùng với salt.
*   **NFR-SEC-002: Phân quyền (Authorization):**
    *   Hệ thống phải thực thi cơ chế phân quyền dựa trên vai trò (Role-Based Access Control - RBAC). Mỗi API endpoint phải được bảo vệ và chỉ cho phép truy cập bởi các vai trò phù hợp (Độc giả, Thủ thư, Admin).
    *   User Service chịu trách nhiệm quản lý vai trò và quyền hạn. Thông tin vai trò được đính kèm trong JWT token và được các service khác kiểm tra khi cần.
*   **NFR-SEC-003: Bảo vệ dữ liệu (Data Protection):**
    *   Giao tiếp giữa client và API Gateway, cũng như giữa API Gateway và các microservice (nếu có thể trong môi trường production) nên được mã hóa bằng HTTPS/TLS để bảo vệ dữ liệu trên đường truyền.
    *   Hệ thống phải có các biện pháp chống lại các cuộc tấn công phổ biến như SQL Injection, Cross-Site Scripting (XSS), Cross-Site Request Forgery (CSRF) bằng cách sử dụng các thư viện/framework an toàn (ví dụ: Spring Security) và tuân thủ các thực hành lập trình an toàn (ví dụ: input validation, output encoding).
*   **NFR-SEC-004: Quản lý Session/Token:**
    *   JWT token phải có thời gian hết hạn ngắn (ví dụ: 15-60 phút) và cơ chế refresh token an toàn để duy trì phiên đăng nhập mà không yêu cầu người dùng đăng nhập lại quá thường xuyên.
    *   Cần có cơ chế để vô hiệu hóa token khi người dùng đăng xuất hoặc khi phát hiện hành vi đáng ngờ (ví dụ: sử dụng blacklist token lưu trong Redis).
*   **NFR-SEC-005: Logging và Audit:**
    *   Hệ thống phải ghi lại (log) các sự kiện bảo mật quan trọng như đăng nhập thành công/thất bại, thay đổi mật khẩu, thay đổi quyền hạn, các thao tác quản trị quan trọng. Log phải bao gồm thông tin về thời gian, người thực hiện, hành động và kết quả.

### 4.3. Khả năng mở rộng (Scalability Requirements)

Kiến trúc microservices được chọn nhằm mục đích chính là tăng khả năng mở rộng của hệ thống.

*   **NFR-SCAL-001: Mở rộng theo chiều ngang (Horizontal Scalability):**
    *   Mỗi microservice (User Service, Book Service, Notification Service) phải được thiết kế để có thể chạy nhiều instance song song.
    *   Hệ thống phải có khả năng dễ dàng tăng hoặc giảm số lượng instance của từng service dựa trên tải thực tế mà không làm gián đoạn hoạt động (ví dụ: sử dụng các công cụ điều phối container như Kubernetes hoặc cơ chế auto-scaling của nền tảng cloud).
    *   Eureka Server và API Gateway (với khả năng load balancing) hỗ trợ việc phân phối tải đến các instance đang hoạt động.
*   **NFR-SCAL-002: Mở rộng Cơ sở dữ liệu:**
    *   Mặc dù mỗi service có database riêng, cần xem xét các chiến lược mở rộng database trong tương lai nếu cần (ví dụ: sử dụng read replicas cho PostgreSQL để giảm tải truy vấn đọc, hoặc sharding nếu dữ liệu quá lớn).
*   **NFR-SCAL-003: Mở rộng Kafka/Redis:**
    *   Kafka và Redis bản thân chúng đã có khả năng mở rộng tốt. Thiết kế hệ thống cần đảm bảo tận dụng được khả năng này khi cần thiết (ví dụ: cấu hình Kafka cluster, Redis cluster).

### 4.4. Tính sẵn sàng (Availability Requirements)

Hệ thống cần đảm bảo hoạt động ổn định và liên tục để phục vụ người dùng.

*   **NFR-AVAIL-001: Độ sẵn sàng:**
    *   Hệ thống phải đạt độ sẵn sàng tối thiểu 99.5% trong giờ hoạt động của thư viện (ví dụ: 8:00 AM - 8:00 PM hàng ngày).
    *   Thời gian downtime dự kiến cho việc bảo trì hoặc nâng cấp phải được lên kế hoạch trước và thông báo cho người dùng, ưu tiên thực hiện ngoài giờ cao điểm.
*   **NFR-AVAIL-002: Khả năng chịu lỗi (Fault Tolerance):**
    *   Sự cố của một instance microservice không được làm sập toàn bộ hệ thống. Các instance khác của cùng service đó phải tiếp tục xử lý yêu cầu.
    *   API Gateway phải triển khai cơ chế Circuit Breaker (ví dụ: sử dụng Resilience4j tích hợp với Spring Cloud Gateway) để cô lập các service đang gặp sự cố và cung cấp phản hồi mặc định (fallback) nếu có thể, tránh làm treo client.
    *   Kafka giúp đảm bảo các thông điệp/sự kiện không bị mất nếu một service tạm thời không hoạt động (khi service hoạt động trở lại, nó có thể tiếp tục xử lý các thông điệp tồn đọng).
*   **NFR-AVAIL-003: Sao lưu và Phục hồi (Backup and Recovery):**
    *   Dữ liệu trong các cơ sở dữ liệu PostgreSQL phải được sao lưu định kỳ (ví dụ: hàng ngày) và lưu trữ an toàn.
    *   Phải có quy trình phục hồi dữ liệu từ bản sao lưu đã được kiểm thử.
    *   Cấu hình của hệ thống cũng cần được sao lưu.

### 4.5. Khả năng sử dụng (Usability Requirements)

Giao diện người dùng phải thân thiện, dễ hiểu và dễ sử dụng cho tất cả các nhóm người dùng.

*   **NFR-USAB-001: Tính nhất quán (Consistency):**
    *   Giao diện người dùng trên toàn bộ ứng dụng phải nhất quán về bố cục, màu sắc, font chữ, thuật ngữ và cách thức tương tác.
*   **NFR-USAB-002: Tính trực quan (Intuitiveness):**
    *   Người dùng (đặc biệt là Độc giả) phải có thể thực hiện các tác vụ cơ bản (tìm sách, xem thông tin, xem lịch sử mượn) mà không cần hướng dẫn chi tiết.
    *   Các nút bấm, menu, thông báo phải rõ ràng và dễ hiểu.
*   **NFR-USAB-003: Phản hồi hệ thống (Feedback):**
    *   Hệ thống phải cung cấp phản hồi rõ ràng cho người dùng sau mỗi hành động (ví dụ: thông báo thành công, thông báo lỗi, chỉ báo đang tải).
*   **NFR-USAB-004: Xử lý lỗi thân thiện (Error Handling):**
    *   Khi có lỗi xảy ra, thông báo lỗi phải dễ hiểu đối với người dùng, giải thích nguyên nhân (nếu có thể) và gợi ý cách khắc phục, thay vì chỉ hiển thị mã lỗi kỹ thuật.
*   **NFR-USAB-005: Khả năng truy cập (Accessibility):**
    *   Giao diện web nên tuân thủ các tiêu chuẩn về khả năng truy cập (ví dụ: WCAG - Web Content Accessibility Guidelines) ở mức độ cơ bản để hỗ trợ người dùng khuyết tật (ví dụ: sử dụng đủ độ tương phản, hỗ trợ điều hướng bằng bàn phím).
*   **NFR-USAB-006: Thiết kế Responsive:**
    *   Giao diện người dùng phải hiển thị tốt và hoạt động ổn định trên các kích thước màn hình khác nhau (máy tính để bàn, máy tính bảng, điện thoại di động).

### 4.6. Khả năng bảo trì (Maintainability Requirements)

Hệ thống cần được thiết kế và xây dựng để dễ dàng bảo trì, sửa lỗi và nâng cấp.

*   **NFR-MAINT-001: Tính module hóa (Modularity):**
    *   Kiến trúc microservices vốn đã đảm bảo tính module hóa cao. Mã nguồn bên trong mỗi service cũng cần được tổ chức thành các module/package rõ ràng, tuân thủ các nguyên tắc thiết kế tốt (ví dụ: SOLID).
*   **NFR-MAINT-002: Tính dễ đọc (Readability):**
    *   Mã nguồn phải được viết rõ ràng, dễ đọc, tuân thủ coding convention (ví dụ: Google Java Style Guide) và có comment giải thích cho các đoạn code phức tạp.
*   **NFR-MAINT-003: Tính dễ kiểm thử (Testability):**
    *   Mỗi microservice phải có bộ kiểm thử đơn vị (unit tests) và kiểm thử tích hợp (integration tests) đầy đủ để đảm bảo chất lượng và dễ dàng phát hiện lỗi khi có thay đổi.
*   **NFR-MAINT-004: Tài liệu hóa (Documentation):**
    *   Ngoài SRS, cần có tài liệu thiết kế chi tiết, tài liệu API, và tài liệu hướng dẫn triển khai, vận hành.
*   **NFR-MAINT-005: Quản lý cấu hình (Configuration Management):**
    *   Các thông tin cấu hình (ví dụ: thông tin kết nối database, Kafka, Redis, key bí mật) phải được quản lý tách biệt khỏi mã nguồn (ví dụ: sử dụng Spring Cloud Config hoặc biến môi trường) để dễ dàng thay đổi giữa các môi trường (dev, staging, production).

### 4.7. Tính di động (Portability Requirements)

*   **NFR-PORT-001: Độc lập Hệ điều hành:**
    *   Hệ thống (được viết bằng Java) về cơ bản là độc lập với hệ điều hành. Tuy nhiên, việc triển khai nên ưu tiên môi trường Linux (Ubuntu LTS).
*   **NFR-PORT-002: Độc lập Trình duyệt:**
    *   Giao diện người dùng web phải tương thích với các phiên bản mới nhất của các trình duyệt phổ biến (Chrome, Firefox, Safari, Edge).
*   **NFR-PORT-003: Đóng gói và Triển khai:**
    *   Mỗi microservice nên được đóng gói dưới dạng container (ví dụ: Docker image) để đảm bảo tính nhất quán và dễ dàng di chuyển giữa các môi trường.

### 4.8. Yêu cầu về Logging và Monitoring

*   **NFR-LOG-001: Logging:**
    *   Tất cả các microservice phải ghi log chi tiết về hoạt động, bao gồm các request nhận được, các lỗi xảy ra, các sự kiện quan trọng. Log nên có định dạng nhất quán (ví dụ: JSON) để dễ dàng thu thập và phân tích.
    *   Sử dụng thư viện logging chuẩn (ví dụ: SLF4j với Logback/Log4j2).
    *   Cần có cơ chế tập trung hóa log từ tất cả các service (ví dụ: sử dụng ELK stack - Elasticsearch, Logstash, Kibana hoặc EFK stack - Elasticsearch, Fluentd, Kibana).
*   **NFR-MON-001: Monitoring:**
    *   Hệ thống phải cung cấp các chỉ số (metrics) về hiệu suất và sức khỏe của từng microservice (ví dụ: thông qua Spring Boot Actuator).
    *   Cần triển khai công cụ giám sát để thu thập, hiển thị và cảnh báo dựa trên các metrics này (ví dụ: Prometheus kết hợp Grafana).
    *   Giám sát cả tài nguyên hạ tầng (CPU, RAM, Disk, Network) của các máy chủ/container.
    *   Cần có cơ chế theo dõi request xuyên suốt các microservice (distributed tracing) để dễ dàng gỡ lỗi (ví dụ: sử dụng Spring Cloud Sleuth kết hợp Zipkin hoặc Jaeger).




## 5. Yêu cầu giao diện (Interface Requirements)

Phần này mô tả các giao diện mà Hệ thống Thư viện Mượn Sách sẽ tương tác, bao gồm giao diện người dùng, giao diện với các thành phần phần mềm khác (nội bộ và bên ngoài), và các giao thức truyền thông được sử dụng.

### 5.1. Giao diện người dùng (User Interface - UI)

*   **UI-001: Loại giao diện:** Hệ thống sẽ cung cấp một giao diện người dùng dựa trên web (web-based UI), có thể truy cập thông qua các trình duyệt web hiện đại.
*   **UI-002: Nguyên tắc thiết kế:**
    *   **Thân thiện và Trực quan:** Giao diện phải dễ hiểu, dễ điều hướng và dễ sử dụng cho tất cả các nhóm người dùng (Độc giả, Thủ thư, Admin), đặc biệt là độc giả với trình độ kỹ thuật đa dạng.
    *   **Nhất quán:** Thiết kế giao diện (bố cục, màu sắc, font chữ, biểu tượng, cách đặt tên các nút và menu) phải nhất quán trên toàn bộ ứng dụng để tạo trải nghiệm liền mạch.
    *   **Responsive:** Giao diện phải tự động điều chỉnh (responsive design) để hiển thị tốt trên các kích thước màn hình khác nhau, bao gồm máy tính để bàn, máy tính bảng và điện thoại di động.
    *   **Phản hồi:** Cung cấp phản hồi trực quan cho người dùng về trạng thái hệ thống (ví dụ: chỉ báo đang tải, thông báo thành công/lỗi).
*   **UI-003: Trình duyệt hỗ trợ:** Giao diện phải tương thích và hoạt động ổn định trên các phiên bản mới nhất của các trình duyệt web phổ biến: Google Chrome, Mozilla Firefox, Apple Safari, Microsoft Edge.
*   **UI-004: Ngôn ngữ:** Ngôn ngữ chính của giao diện là Tiếng Việt.
*   **UI-005: Mockups/Wireframes:** Các bản phác thảo giao diện (wireframes) hoặc thiết kế chi tiết (mockups) cho các màn hình chính (ví dụ: trang chủ, tìm kiếm, chi tiết sách, đăng nhập, đăng ký, quản lý tài khoản, dashboard admin/thủ thư) sẽ được cung cấp trong Phụ lục (Appendix) hoặc tài liệu thiết kế UI riêng biệt.

### 5.2. Giao diện phần cứng (Hardware Interface)

*   **HW-001:** Hiện tại, không có yêu cầu cụ thể nào về giao diện trực tiếp với phần cứng chuyên dụng. Hệ thống là một ứng dụng web tiêu chuẩn, hoạt động trên các thiết bị có trình duyệt web (máy tính, điện thoại, máy tính bảng).
*   **HW-002 (Tiềm năng):** Nếu trong quá trình triển khai thực tế, thư viện có sử dụng các thiết bị như máy quét mã vạch (barcode scanner) cho thủ thư để nhập mã thẻ độc giả hoặc mã sách, thì các thiết bị này thường hoạt động như thiết bị nhập liệu chuẩn (giống bàn phím) và không yêu cầu giao diện phần cứng đặc biệt từ phía ứng dụng web.

### 5.3. Giao diện phần mềm (Software Interface)

*   **SW-INT-001: Giao tiếp Nội bộ (Internal Microservices Communication):**
    *   **API Gateway:** Là điểm vào duy nhất cho mọi yêu cầu từ client (trình duyệt web). Nó định tuyến các yêu cầu đến các microservice nội bộ phù hợp (User Service, Book Service, Notification Service) thông qua giao thức HTTP/HTTPS.
    *   **RESTful APIs:** Các microservice (User Service, Book Service, Notification Service) cung cấp các API RESTful để các service khác hoặc API Gateway có thể gọi (chủ yếu cho các tương tác đồng bộ). Định dạng dữ liệu trao đổi qua API là JSON. Chi tiết các API endpoints được mô tả trong tài liệu `/home/ubuntu/library-system/docs/microservices-design.md`.
    *   **Service Discovery (Eureka):** Các microservice đăng ký địa chỉ của chúng với Eureka Server khi khởi động. API Gateway và các service khác sử dụng Eureka Client để khám phá địa chỉ của các service cần gọi, hỗ trợ load balancing.
    *   **Asynchronous Communication (Kafka):** Các microservice sử dụng Kafka để giao tiếp bất đồng bộ cho các sự kiện như `user-created`, `book-borrowed`, `book-returned`, `reservation-available`, v.v. Định dạng message trên Kafka là JSON. Chi tiết về các topic và cấu trúc message được mô tả trong tài liệu `/home/ubuntu/library-system/docs/microservices-design.md`.
    *   **Common Library:** Các DTOs (Data Transfer Objects) và các lớp tiện ích chung được định nghĩa trong Common Library và được sử dụng bởi các microservice để đảm bảo tính nhất quán khi trao đổi dữ liệu.
*   **SW-EXT-001: Giao diện Email:**
    *   Notification Service sẽ tương tác với một máy chủ SMTP (Simple Mail Transfer Protocol) để gửi email thông báo đến người dùng. Giao diện này sử dụng thư viện JavaMail (được tích hợp trong Spring Mail). Cấu hình chi tiết của SMTP server (host, port, authentication) sẽ được thực hiện bên ngoài mã nguồn.
*   **SW-EXT-002: Giao diện SMS/Push Notification (Tiềm năng):**
    *   Nếu chức năng gửi SMS hoặc Push Notification được triển khai, Notification Service sẽ cần tích hợp với API của các nhà cung cấp dịch vụ bên thứ ba (ví dụ: Twilio API cho SMS, Firebase Cloud Messaging (FCM) API hoặc Apple Push Notification Service (APNS) API cho push notification). Đặc tả chi tiết của các API này sẽ phụ thuộc vào nhà cung cấp được chọn.

### 5.4. Giao diện truyền thông (Communication Interface)

*   **COMM-001: Giao thức Mạng:**
    *   **HTTP/HTTPS:** Được sử dụng cho tất cả giao tiếp giữa client (trình duyệt) và API Gateway, cũng như cho giao tiếp RESTful giữa API Gateway và các microservice nội bộ. HTTPS được khuyến nghị mạnh mẽ cho môi trường production để đảm bảo an toàn.
    *   **TCP/IP:** Là giao thức nền tảng cho HTTP/HTTPS và cũng được Kafka sử dụng cho giao tiếp giữa producer/consumer và Kafka broker.
*   **COMM-002: Định dạng Dữ liệu:** JSON là định dạng dữ liệu chuẩn được sử dụng cho các payload trong API RESTful và các message trên Kafka.
*   **COMM-003: API Endpoints:**
    *   API Gateway sẽ expose các API endpoints công khai cho client. Các endpoints này được định nghĩa trong cấu hình của Spring Cloud Gateway và ánh xạ tới các API nội bộ của từng microservice. Ví dụ:
        *   `/api/auth/**` -> User Service
        *   `/api/users/**` -> User Service
        *   `/api/books/**` -> Book Service
        *   `/api/borrowings/**` -> Book Service
        *   `/api/notifications/**` -> Notification Service
    *   Chi tiết về các endpoints, phương thức (GET, POST, PUT, DELETE), tham số và cấu trúc request/response có thể truy cập thông qua API Gateway.
*   **COMM-004: Ports Mặc định:**
    *   API Gateway: 8080
    *   Eureka Server: 8761
    *   Kafka Brokers: 9092
    *   PostgreSQL: 5432
    *   Redis: 6379
    *   (Các port của User, Book, Notification Service sẽ được cấp phát động hoặc cấu hình cụ thể khi triển khai).




## 6. Các trường hợp sử dụng (Use Cases)

Phần này mô tả chi tiết các kịch bản tương tác chính giữa người dùng (tác nhân) và hệ thống để đạt được một mục tiêu cụ thể. Mỗi Use Case bao gồm mục tiêu, tác nhân chính, các điều kiện tiên quyết, luồng sự kiện chính và các luồng phụ hoặc ngoại lệ.

(Lưu ý: Sơ đồ Use Case tổng quát và chi tiết sẽ được cung cấp trong Phụ lục).

### 6.1. Use Case: Đăng ký tài khoản Độc giả
*   **UC-01: Đăng ký tài khoản Độc giả**
*   **Mục tiêu:** Cho phép một người dùng mới tạo tài khoản độc giả trong hệ thống.
*   **Tác nhân chính:** Người dùng chưa có tài khoản (Guest User).
*   **Điều kiện tiên quyết:** Người dùng có thể truy cập trang đăng ký của hệ thống.
*   **Kết quả mong đợi (Thành công):**
    *   Tài khoản độc giả mới được tạo trong hệ thống.
    *   Người dùng nhận được thông báo đăng ký thành công.
    *   Người dùng nhận được email chào mừng (nếu cấu hình).
    *   Người dùng được chuyển hướng đến trang đăng nhập hoặc trang thông tin cá nhân.
*   **Luồng sự kiện chính:**
    1.  Người dùng chọn chức năng "Đăng ký" trên giao diện.
    2.  Hệ thống hiển thị form đăng ký yêu cầu các thông tin: tên đăng nhập, email, mật khẩu, xác nhận mật khẩu, họ, tên (bắt buộc) và số điện thoại (tùy chọn).
    3.  Người dùng nhập đầy đủ thông tin vào form.
    4.  Người dùng nhấn nút "Đăng ký".
    5.  Hệ thống xác thực thông tin đầu vào (tham chiếu FR-USER-001: Ràng buộc và xử lý lỗi).
    6.  Hệ thống tạo tài khoản mới với vai trò "Độc giả".
    7.  Hệ thống lưu thông tin tài khoản.
    8.  Hệ thống gửi sự kiện `user-created` đến Kafka.
    9.  Hệ thống hiển thị thông báo thành công và chuyển hướng người dùng.
*   **Luồng phụ/Ngoại lệ:**
    *   **5a. Thông tin không hợp lệ:**
        1.  Hệ thống phát hiện thông tin không hợp lệ (trùng tên đăng nhập/email, mật khẩu yếu, thiếu trường bắt buộc, email sai định dạng, xác nhận mật khẩu không khớp).
        2.  Hệ thống hiển thị thông báo lỗi cụ thể tương ứng với từng trường không hợp lệ ngay trên form đăng ký.
        3.  Use case kết thúc (người dùng cần sửa lại thông tin).
    *   **7a. Lỗi hệ thống khi lưu tài khoản:**
        1.  Hệ thống gặp lỗi khi cố gắng lưu thông tin vào cơ sở dữ liệu (ví dụ: mất kết nối database).
        2.  Hệ thống ghi log lỗi.
        3.  Hệ thống hiển thị thông báo lỗi chung ("Đã có lỗi xảy ra, vui lòng thử lại sau.").
        4.  Use case kết thúc.

### 6.2. Use Case: Đăng nhập hệ thống
*   **UC-02: Đăng nhập hệ thống**
*   **Mục tiêu:** Cho phép người dùng đã có tài khoản (Độc giả, Thủ thư, Admin) truy cập vào hệ thống.
*   **Tác nhân chính:** Người dùng đã đăng ký (Độc giả, Thủ thư, Admin).
*   **Điều kiện tiên quyết:** Người dùng đã có tài khoản hợp lệ và đang ở trang đăng nhập.
*   **Kết quả mong đợi (Thành công):**
    *   Hệ thống xác thực người dùng thành công.
    *   Người dùng nhận được JWT token.
    *   Người dùng được chuyển hướng đến trang làm việc chính tương ứng với vai trò của họ.
*   **Luồng sự kiện chính:**
    1.  Người dùng nhập tên đăng nhập (hoặc email) và mật khẩu vào form đăng nhập.
    2.  Người dùng nhấn nút "Đăng nhập".
    3.  Hệ thống gửi thông tin đăng nhập đến User Service để xác thực.
    4.  User Service kiểm tra thông tin đăng nhập với dữ liệu trong cơ sở dữ liệu.
    5.  User Service tạo JWT token chứa thông tin người dùng và vai trò.
    6.  User Service trả token về cho client.
    7.  Client lưu token và chuyển hướng người dùng đến trang phù hợp.
*   **Luồng phụ/Ngoại lệ:**
    *   **4a. Thông tin đăng nhập không chính xác:**
        1.  User Service xác định tên đăng nhập/email hoặc mật khẩu không đúng.
        2.  Hệ thống hiển thị thông báo lỗi "Tên đăng nhập hoặc mật khẩu không chính xác".
        3.  Use case kết thúc.
    *   **4a. Tài khoản bị khóa/chưa kích hoạt:**
        1.  User Service xác định tài khoản đang ở trạng thái không cho phép đăng nhập.
        2.  Hệ thống hiển thị thông báo lỗi tương ứng (ví dụ: "Tài khoản của bạn đã bị khóa.").
        3.  Use case kết thúc.
    *   **3a. Lỗi kết nối hoặc lỗi User Service:**
        1.  Hệ thống không thể kết nối đến User Service hoặc User Service trả về lỗi.
        2.  Hệ thống ghi log lỗi.
        3.  Hệ thống hiển thị thông báo lỗi chung.
        4.  Use case kết thúc.

### 6.3. Use Case: Tìm kiếm Sách
*   **UC-03: Tìm kiếm Sách**
*   **Mục tiêu:** Cho phép người dùng tìm kiếm các đầu sách trong thư viện.
*   **Tác nhân chính:** Độc giả, Thủ thư, Admin.
*   **Điều kiện tiên quyết:** Người dùng đã truy cập vào giao diện tìm kiếm sách.
*   **Kết quả mong đợi (Thành công):** Hệ thống hiển thị danh sách các sách phù hợp với tiêu chí tìm kiếm của người dùng.
*   **Luồng sự kiện chính:**
    1.  Người dùng nhập từ khóa vào ô tìm kiếm.
    2.  (Tùy chọn) Người dùng áp dụng các bộ lọc nâng cao (thể loại, tác giả, năm xuất bản...). 
    3.  Người dùng nhấn nút "Tìm kiếm".
    4.  Hệ thống gửi yêu cầu tìm kiếm (bao gồm từ khóa và bộ lọc) đến Book Service.
    5.  Book Service thực hiện truy vấn tìm kiếm trong cơ sở dữ liệu.
    6.  Book Service trả về danh sách các sách phù hợp (có phân trang).
    7.  Hệ thống hiển thị kết quả tìm kiếm cho người dùng, bao gồm thông tin tóm tắt và trạng thái sẵn có.
*   **Luồng phụ/Ngoại lệ:**
    *   **6a. Không tìm thấy kết quả:**
        1.  Book Service không tìm thấy sách nào phù hợp.
        2.  Hệ thống hiển thị thông báo "Không tìm thấy sách nào phù hợp với tiêu chí của bạn".
        3.  Use case kết thúc.
    *   **4a. Lỗi kết nối hoặc lỗi Book Service:**
        1.  Hệ thống không thể gửi yêu cầu đến Book Service hoặc Book Service trả về lỗi.
        2.  Hệ thống ghi log lỗi.
        3.  Hệ thống hiển thị thông báo lỗi chung.
        4.  Use case kết thúc.

### 6.4. Use Case: Mượn Sách (Tại quầy)
*   **UC-04: Mượn Sách (Tại quầy)**
*   **Mục tiêu:** Cho phép Thủ thư ghi nhận việc một Độc giả mượn một bản sao sách.
*   **Tác nhân chính:** Thủ thư.
*   **Tác nhân phụ:** Độc giả.
*   **Điều kiện tiên quyết:**
    *   Thủ thư đã đăng nhập vào hệ thống.
    *   Độc giả có mặt tại quầy với thẻ thư viện hợp lệ.
    *   Bản sao sách cần mượn có sẵn tại quầy.
*   **Kết quả mong đợi (Thành công):**
    *   Giao dịch mượn sách được ghi nhận.
    *   Trạng thái bản sao sách được cập nhật thành "Đang mượn".
    *   Độc giả nhận được thông báo xác nhận (email).
    *   Thủ thư nhận được thông báo thành công trên màn hình.
*   **Luồng sự kiện chính:**
    1.  Thủ thư chọn chức năng "Mượn sách".
    2.  Thủ thư nhập/quét mã thẻ Độc giả.
    3.  Hệ thống kiểm tra thông tin Độc giả và điều kiện mượn (thẻ hợp lệ, chưa quá giới hạn mượn - tham chiếu FR-BRW-001).
    4.  Thủ thư nhập/quét mã bản sao sách.
    5.  Hệ thống kiểm tra trạng thái bản sao sách (phải là "Có sẵn" - tham chiếu FR-BRW-001).
    6.  Hệ thống ghi nhận giao dịch mượn, cập nhật trạng thái bản sao sách, tính hạn trả.
    7.  Hệ thống lưu giao dịch.
    8.  Hệ thống gửi sự kiện `book-borrowed` đến Kafka.
    9.  Hệ thống hiển thị thông báo mượn thành công cho Thủ thư.
*   **Luồng phụ/Ngoại lệ:**
    *   **3a. Độc giả không đủ điều kiện mượn:**
        1.  Hệ thống phát hiện thẻ không hợp lệ hoặc đã mượn quá giới hạn.
        2.  Hệ thống hiển thị thông báo lỗi cụ thể cho Thủ thư.
        3.  Use case kết thúc.
    *   **5a. Sách không có sẵn:**
        1.  Hệ thống phát hiện bản sao sách không ở trạng thái "Có sẵn".
        2.  Hệ thống hiển thị thông báo lỗi "Sách này hiện không có sẵn để mượn".
        3.  Use case kết thúc.
    *   **7a. Lỗi hệ thống:**
        1.  Hệ thống gặp lỗi khi kiểm tra thông tin, lưu giao dịch hoặc gửi sự kiện.
        2.  Hệ thống ghi log lỗi.
        3.  Hệ thống hiển thị thông báo lỗi chung cho Thủ thư.
        4.  Use case kết thúc.

### 6.5. Use Case: Trả Sách (Tại quầy)
*   **UC-05: Trả Sách (Tại quầy)**
*   **Mục tiêu:** Cho phép Thủ thư ghi nhận việc Độc giả trả lại một bản sao sách đã mượn.
*   **Tác nhân chính:** Thủ thư.
*   **Tác nhân phụ:** Độc giả.
*   **Điều kiện tiên quyết:**
    *   Thủ thư đã đăng nhập vào hệ thống.
    *   Độc giả mang sách cần trả đến quầy.
*   **Kết quả mong đợi (Thành công):**
    *   Giao dịch mượn được cập nhật trạng thái "Đã trả".
    *   Trạng thái bản sao sách được cập nhật ("Có sẵn" hoặc "Đặt trước").
    *   Phí phạt (nếu có) được tính và hiển thị.
    *   Độc giả nhận được thông báo xác nhận (email).
    *   Thủ thư nhận được thông báo thành công trên màn hình.
*   **Luồng sự kiện chính:**
    1.  Thủ thư chọn chức năng "Trả sách".
    2.  Thủ thư nhập/quét mã bản sao sách.
    3.  Hệ thống tìm giao dịch mượn đang hoạt động của sách này.
    4.  Hệ thống ghi nhận ngày trả, cập nhật trạng thái giao dịch.
    5.  Hệ thống kiểm tra và tính phí phạt nếu trả muộn.
    6.  Hệ thống cập nhật trạng thái bản sao sách (kiểm tra xem có đặt trước không - tham chiếu FR-BRW-002).
    7.  Hệ thống lưu các thay đổi.
    8.  Hệ thống gửi sự kiện `book-returned` (và `reservation-available` nếu cần) đến Kafka.
    9.  Hệ thống hiển thị thông báo trả thành công cho Thủ thư, kèm thông tin phí phạt nếu có.
*   **Luồng phụ/Ngoại lệ:**
    *   **3a. Không tìm thấy giao dịch mượn:**
        1.  Hệ thống không tìm thấy giao dịch mượn nào đang hoạt động cho bản sao sách này.
        2.  Hệ thống hiển thị thông báo lỗi "Không tìm thấy thông tin mượn cho sách này".
        3.  Use case kết thúc.
    *   **7a. Lỗi hệ thống:**
        1.  Hệ thống gặp lỗi khi cập nhật dữ liệu hoặc gửi sự kiện.
        2.  Hệ thống ghi log lỗi.
        3.  Hệ thống hiển thị thông báo lỗi chung cho Thủ thư.
        4.  Use case kết thúc.

### 6.6. Use Case: Đặt trước Sách
*   **UC-06: Đặt trước Sách**
*   **Mục tiêu:** Cho phép Độc giả đặt trước một đầu sách hiện không có bản sao nào sẵn có.
*   **Tác nhân chính:** Độc giả.
*   **Điều kiện tiên quyết:**
    *   Độc giả đã đăng nhập vào hệ thống.
    *   Độc giả đang xem chi tiết một đầu sách mà tất cả các bản sao đều đang được mượn.
    *   Độc giả đủ điều kiện đặt trước (chưa quá giới hạn, không nợ phạt...).
*   **Kết quả mong đợi (Thành công):**
    *   Yêu cầu đặt trước được tạo và ghi nhận vào hệ thống.
    *   Độc giả nhận được thông báo đặt trước thành công trên màn hình và qua email.
*   **Luồng sự kiện chính:**
    1.  Độc giả nhấn nút "Đặt trước" trên trang chi tiết sách.
    2.  Hệ thống kiểm tra điều kiện đặt trước của Độc giả (tham chiếu FR-RSV-001).
    3.  Hệ thống tạo yêu cầu đặt trước mới cho đầu sách này, liên kết với Độc giả.
    4.  Hệ thống lưu yêu cầu đặt trước.
    5.  Hệ thống gửi sự kiện `book-reserved` đến Kafka.
    6.  Hệ thống hiển thị thông báo đặt trước thành công.
*   **Luồng phụ/Ngoại lệ:**
    *   **2a. Không đủ điều kiện đặt trước:**
        1.  Hệ thống phát hiện Độc giả không đủ điều kiện (ví dụ: đã đặt sách này rồi, quá giới hạn đặt trước).
        2.  Hệ thống hiển thị thông báo lỗi cụ thể.
        3.  Use case kết thúc.
    *   **4a. Lỗi hệ thống:**
        1.  Hệ thống gặp lỗi khi lưu yêu cầu đặt trước hoặc gửi sự kiện.
        2.  Hệ thống ghi log lỗi.
        3.  Hệ thống hiển thị thông báo lỗi chung.
        4.  Use case kết thúc.

*(Có thể bổ sung thêm các Use Case chi tiết khác như: Quản lý sách (Admin/Librarian), Quản lý người dùng (Admin), Xem lịch sử mượn, Hủy đặt trước, Cấu hình hệ thống (Admin)... tùy thuộc vào mức độ chi tiết yêu cầu)*
```text
book-service/
├── src/main/java/com/library/book/
├── application/
│   ├── service/
│   └── dto/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── event/
│   └── exception/
├── infrastructure/
│   ├── persistence/
│   ├── messaging/
│   └── security/
└── interfaces/
├── rest/
├── event/
└── scheduler/
```