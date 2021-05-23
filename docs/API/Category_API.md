# `Category` API Document

## Lấy ra danh sách các thể loại hiện có

----
Trả về 1 danh sách các thể loại kèm theo số lượng bài viết tương ứng với từng thể loại

* **URL**: `/category`

* **Method:** `GET`

* **Request Params:**

  | Name     | Type  | Description                | Default                |
  | -------- |:----: | ------------               | -------                |
  | `page`   | `int` | Index của page             | 0                      |
  | `size`   | `int` | Số lượng `category` trả về | `2^31-1` (lấy toàn bộ) | 


* **Success Response:**

    - **Code:** `200 OK` - Kèm 1 danh sách các thể loại được sắp xếp theo tên

    * **Example:** `GET /category`
    
```json5
[
  {
    "categoryId": 7,
    "categoryName": "Du lịch Huế",
    "postCount": 1
  },
  {
    "categoryId": 6,
    "categoryName": "Du lịch Hà Nội",
    "postCount": 2
  },
]
```

## Lấy ra danh sách các bài viết theo thể loại

----
Lấy ra danh sách các bài viết theo một thể loại nào đó (giống như danh sách bài viết ở trang chủ vậy)

* **URL**: `/category/{category_id}/post`

* **Method:** `GET`

* **Request Params**

  | Name    | Type    | Description               | Default   |
  | ------- |:------: | ------------              | :-------: |
  | `page`  | `int`   | Số thứ tự trang           | 0         |
  | `size`  | `int`   | Số lượng bài viết muốn lấy| 10        |

* **Success Response:**

    * **Code:** `200 OK` - Kèm theo danh sách các bài viết (xem lại ở phần bài viết)
        
* **Error Response:**

    * **Code**: `404 NOT_FOUND` - Thể loại không tồn tại

## Thêm một thể loại mới (Dành cho `ADMIN`)

----
Thêm một thể loại mới

* **URL**: `/category`

* **Method:** `POST`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name     | Type       | Description  |
  | -------  |:------:    | ------------ |
  | `name`   | `string`   | Tên thể loại |

* **Response:**

  * **Success Response:**

    * **Code:** `200 OK` - Kèm theo thông tin của thể loại vừa thêm vào (Xem lại ở phần trên)

  * **Error Response:**

    * **Code**: `409 CONFLICT` - Thể loại đã tồn tại
      
    * **Code**: `403 FORBIDDEN` - Không có quyền

## Xoá một thể loại (dành cho `ADMIN`)

----
Xoá một thể loại

* **URL**: `/category/{category_id}`

* **Method:** `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `204 NO_CONTENT` - Xoá thành công
  
* **Error Response:**

    * **Code**: `404 NOT_FOUND` - Thể loại không tồn tại

    * **Code**: `403 FORBIDDEN` - Không có quyền

## Sửa tên một thể loại (dành cho `ADMIN`)

----
Sửa tên của thể loại

* **URL**: `/category/{category_id}`

* **Method:** `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name     | Type       | Description  |
  | -------  |:------:    | ------------ |
  | `name`   | `string`   | Tên thể loại |


* **Success Response:**

  * **Code:** `204 NO_CONTENT` - Sửa tên thành công

* **Error Response:**

  * **Code**: `404 NOT_FOUND` - Thể loại không tồn tại

  * **Code**: `403 FORBIDDEN` - Không có quyền