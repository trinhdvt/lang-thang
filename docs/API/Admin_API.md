# `ADMIN` API Document - chỉ `ADMIN` mới được gọi

## Lấy ra các thông tin cơ bản về website

----
Trả về các thông tin cơ bản về website

* **URL**: `/system/info`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `200 OK` - Kèm thông tin cơ bản của website

    * **Example:** `GET /system/info`
    
```json5
{
  "userCount": 7, // số lượng người dùng trong hệ thống
  "postCount": 25, // số lượng bài viết 
  "reportedPostCount": 4 // số lượng báo cáo
}
```

* **Error Response:**

  * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`

## Lấy ra top các User được theo dõi nhiều nhất

----
Danh sách các User có số lượng theo dõi nhiều nhất (mặc định là 5 người)

* **URL**: `/user/follow/top`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

  | Name    | Type    | Description                 | Default |
  | ------- |:------: | ------------                | ------- |
  | `size`  | `int`   | Số lượng user muốn lấy      | 5       |

* **Success Response:**

  * **Code:** `200 OK` - Kèm theo một danh sách thông tin chi tiết của các User

  * **Example:** Xem lại phần User

* **Error Response:**
 
  * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`

## Lấy ra danh sách các User trong hệ thống

----
Trả về danh sách chi tiết các User trong hệ thống

* **URL**: `/system/users`

* **Method:** `GET`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Params**

  | Name    | Type    | Description            | Default |
  | ------- |:------: | ------------           | ------- |
  | `page`  | `int`   | Index của trang        | 0       |
  | `size`  | `int`   | Số lượng User muốn lấy | 10      |

* **Success Response:**

  * **Code:** `200 OK` - Kèm theo một danh sách thông tin chi tiết của các User

  * **Example:** Xem lại phần User

* **Error Response:**

  * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`

## Thăng cấp 1 User lên làm ADMIN

----
Thăng cấp 1 User lên thành ADMIN

* **URL**: `/user/{user_id}/admin`

* **Method:** `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Response:**

  * **Success Response:**

    * **Code:** `202 ACCEPTED` - Thăng cấp thành công

  * **Error Response:**
  
    * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`
      
    * **Code**: `422 UNPROCESSABLE_ENTITY` - Accout không tồn tại hoặc chưa kích hoạt tài khoản