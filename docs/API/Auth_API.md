# Authentication API Document

## Table of contents

* [Đăng nhập](#đăng-nhập)
* [Đăng nhập với Google](#đăng-nhập-bằng-tài-khoản-Google)
* [Lấy lại Token mới](#Lấy-lại-Token-mới)
* [Đăng ký](#Đăng-ký)
* [Xác thực đăng ký](#Xác-thực-đăng-ký)
* [Yêu cầu quên mật khẩu](#Quên-mật-khẩu)
* [Xác thực mật khẩu mới](#thay-đổi-mật-khẩu)

## Đăng nhập

----
Trả về token hợp lệ

* **URL**: `/auth/login`

* **Method:** `POST`

* **Request Params** `Content-Type: multipart/form-data`

  | Name         | Type     | Description          |
  | ----------   |:------:  | ------------         |
  | `email`      | `string` | Email của người dùng |
  | `password`   | `string` | Mật khẩu             |

* **Success Response:**

    - **Code:** 200 
      
      **Content:** `chuỗi token` kèm theo cookie được gắn vào

    * **Example:**

    ```json5
    {
    'token': 'asdasdas21312$#...',
    'duration': '10000 - hạn sử dụng của token tính bằng ms'
    }
  ```

* **Error Response:**

    * **Code:** 400 BAD REQUEST - Email hoặc password không hợp lệ
    
    * **Code:** 401 UNAUTHORIZED - Sai email hoặc password

    * **Code:** 423 LOCKED - Email đã đăng ký nhưng chưa kích hoạt

## Đăng nhập bằng tài khoản Google

----
Trả về token hợp lệ

- Nếu người dùng chưa đăng nhập lần nào thì sẽ tự động tạo một tài khoản dựa trên các thông tin công khai của tài khoản
  Google với mật khẩu ngẫu nhiên, mật khẩu này sẽ được gửi qua email cho người dùng. Sau đó tiến hành đăng nhập bình
  thường

- Nếu người dùng đã có tài khoản thì tiến hành đăng nhập bình thường không cần dùng mật khẩu

- Nếu người dùng đã tạo tài khoản nhưng chưa active thì sẽ active và đăng nhập bình thường

* **URL**: `/auth/google`

* **Method:** `POST`

* **Request Params** `Content-Type: multipart/form-data`

  | Name          | Type     | Description                 |
  | ----------    |:------:  | ------------                |
  | `google_token`| `string` | Google Token của người dùng |

* **Success Response:**

    * **Code:** 200 
      
      **Content:** `chuỗi token` kèm theo cookie được gắn vào
    
    * **Example**: xem lại ở phần [Đăng nhập](#đăng-nhập)

* **Error Response:**

    * **Code:** 401 UNAUTHORIZED - Google token không hợp lệ

## Lấy lại Token mới

----
Trả về lại một token hợp lệ khác sau khi đã đăng nhập

* **URL**: `/auth/refreshToken`

* **Method:** `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Cookie**: `refresh-token: Mã refresh token được server gắn vào sau khi đăng nhập thành công`

* **Success Response:**

    * **Code:** 200 
      
      **Content:** `chuỗi token mới` kèm theo cookie mới được gắn vào header
        
    * **Example**: xem lại ở phần [Đăng nhập](#đăng-nhập)

* **Error Response:**

    * **Code:** 400 BAD REQUEST - Cookie rỗng
      
    * **Code:** 403 FORBIDDEN - Chưa đăng nhập      

    * **Code:** 401 UNAUTHORIZED - `Refresh-token` không hợp lệ    

## Đăng ký

----
Đăng ký tài khoản với hệ thống

* **URL**: `/auth/registration`

* **Method:**: `POST`

* **Request Params** `Content-Type: multipart/form-data`

| Name                | Type     | Description                                    |
| ----------          |:------:  | ------------                                   |
| `name`              | `string` | Tên hiển thị                                   |
| `email`             | `string` | Email của người dùng (đúng định dạng email)    |
| `password`          | `string` | Mật khẩu (ít nhất 6 ký tự, nhiều nhất 32 kí tự)|
| `matchedPassword`   | `string` | Mật khẩu nhập lại lần 2                        |

* **Success Response:**

    * **Code:** 202 ACCEPTED - 1 link active (kèm `token`) sẽ được gửi vào email
    
* **Error Response:**

    * **Code:** 400 BAD REQUEST - Dữ liệu đầu vào không hợp lệ

    * **Code:** 409 CONFLICT - Email đã tồn tại

    * **Code:** 423 LOCKED - Email đã đăng ký nhưng chưa kích hoạt

## Xác thực đăng ký

----
Xác thực đăng ký với hệ thống bằng token đã được gửi trong email

* **URL**: `/auth/registrationConfirm`

* **Method:**: `POST`

* **Request Params** `Content-Type: multipart/form-data`

| Name      | Type     | Description                     |
| ----------|:------:  | ------------                    |
| `token`   | `string` | Token dùng để xác nhận đăng ký  |

* **Success Response:**

  * **Code:** 202 ACCEPTED - Xác nhận đăng ký thành công

* **Error Response:**

  * **Code:** 401 UNAUTHORIZED - Token không hợp lệ

  * **Code:** 410 GONE - Token hết hạn

## Quên mật khẩu

----
- Người dùng gửi yêu cầu thay đổi mật khẩu mới khi quên mật khẩu

* **URL**: `/auth/resetPassword`

* **Method:**: `POST`

* **Request Params** `Content-Type: multipart/form-data`

| Name         | Type     | Description          |
| ----------   |:------:  | ------------         |
| `email`      | `string` | Email của người dùng |

* **Success Response:**

    * **Code:** 202 ACCEPTED - 1 link reset password (có kèm `token`) sẽ được gửi vào email (`/auth/resetPassword/{reset_password_token}`)
    
* **Error Response:**

    * **Code:** 400 BAD REQUEST - Email không đúng định dạng
  
    * **Code:** 423 LOCKED - Tài khoản chưa kích hoạt

    * **Code:** 404 NOT_FOUND - Email không tồn tại

## Xác nhận `reset_password_token`

----
- Xác nhận xem thử token có hợp lệ để thực hiện yêu cầu đổi mật khẩu hay không

* **URL**: `/auth/resetPassword`

* **Method:**: `GET`

* **Request Params**

| Name         | Type     | Description                  |
| ----------   |:------:  | ------------                 |
| `token`      | `string` | Token dùng để reset mật khẩu |

* **Success Response:**

  * **Code:** 202 ACCEPTED - Token hợp lệ, có thể tiến hành đổi mật khẩu

* **Error Response:**

  * **Code:** 401 UNAUTHORIZED - Token không tồn tại

  * **Code:** 410 GONE - Token hết hạn, yêu cầu quay lại trang chủ để thực hiện lại yêu cầu

## Thay đổi mật khẩu

---
Thay đổi mật khẩu trong trường hợp bị quên

* **URL**: `/auth/resetPassword`

* **Method:**: `PUT`

* **Request Params** `Content-Type: multipart/form-data`

| Name              | Type     | Description             |
| ----------        |:------:  | ------------            |
| `token`           | `string` | Token để reset password |
| `password`        | `string` | Mật khẩu mới            |
| `matchedPassword` | `string` | Nhập lại mật khẩu       |

* **Success Response:**

  * **Code:** 202 ACCEPTED - thay đổi mật khẩu thành công

* **Error Response:**

  * **Code:** 400 BAD REQUEST - Mật khẩu không đủ 6 kí tự / thiếu tham số

  * **Code:** 401 UNAUTHORIZED - Token không hợp lệ  
  
  * **Code:** 410 GONE - Token hết hạn
