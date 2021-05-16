# `Bookmark` API Document

## Lấy ra danh sách các bài viết đã bookmark

----
Trả về 1 danh sách các bài viết đã bookmark (giống như ở trang chủ vậy)

* **URL**: `/bookmark/posts`

* **Method:** `GET`

* **Headers:** `Authorization: Bearer <token hiện tại>`

* **Request Param**:

  | Name         | Type       | Description                     | Default |
  | ----------   |:------:    | ------------                    | ------- |
  | `size`       | `int >= 0` | Số lượng bài viết muốn lấy về   | 10      |
  | `page`       | `int >= 0` | Index của trang                 | 0       |


* **Success Response:**

    - **Code:** `200 OK` - Kèm 1 danh sách các các bài viết (Xem lại ở phần bài viết)
  
* **Error Response:**

    * **Code**: `403 FORBIDDEN` - Chưa đăng nhập

## Bookmark một bài viết

----
Bookmark một bài viết

* **URL**: `/bookmark`

* **Method:** `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name       | Type       | Description                     |
  | ---------- |:------:    | ------------                    |
  | `postId`  | `string`   | Id của bài viết muốn Bookmark   |

* **Success Response:**

    * **Code:** `202 ACCEPTED` - Kèm số lượng bookmark của bài viết đó
      
    * **Code:** `204 NO_CONTENT` - Bài viết đã bookmark sẵn rồi
     
* **Error Response:**

    * **Code**: `404 NOT_FOUND` - Bài viết không tồn tại

    * **Code**: `403 FORBIDDEN` - chưa đăng nhập

## Xoá một bookmark

----
Xoá bài viết ra khỏi danh sách các bài đã bookmark

* **URL**: `/bookmark`

* **Method:** `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name       | Type       | Description                     |
  | ---------- |:------:    | ------------                    |
  | `postId`  | `string`   | Id của bài viết muốn Bookmark   |

* **Response:** Giống y như phần trên