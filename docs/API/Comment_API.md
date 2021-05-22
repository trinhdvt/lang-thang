# `Comment` API Document

## Lấy ra danh sách các comment của một bài viết

----
Trả về 1 danh sách các comment của một bài viết (được sắp xếp tăng dần theo ngày comment)

* **URL**: `/comment/post/{post_id}`

* **Method:** `GET`

* **Request Params:**

  | Name     | Type  | Description             | Default |
  | -------- |:----: | ------------            | ------- |
  | `page`   | `int` | Index của page          | 0       |
  | `size`   | `int` | Số lượng comment trả về | 10      | 

* **Success Response:**

    - **Code:** `200 OK` - Kèm 1 danh sách các Comment thuộc bài viết đó

    * **Example:** `GET /comment/post/3`
    
```json5
[
  {
    "commentId": 7,
    "commenter": {
      "accountId": 4,
      "name": "Trinhdvt2",
      "email": "trinhdvt2@gmail.com",
      "postCount": 0,
      "followCount": 0,
      "bookmarkOnOwnPostCount": 0,
      "commentOnOwnPostCount": 0,
      "fbLink": null,
      "instagramLink": null,
      "avatarLink": "https://lh3.googleusercontent.com/a/AATXAJwep-n67Wo_25OpuJb5x2jIzRMz8tj7uCEfGMR2=s96-c",
      "about": null,
      "occupation": null
    },
    "commentDate": "2021-05-06 15:27",
    "content": "Bình tĩnh mấy bro",
    "likeCount": 1,
    "liked": false,
    "myComment": true
  },
  {
    "commentId": 11,
    "commenter": {
      "accountId": 4,
      "name": "Trinhdvt2",
      "email": "trinhdvt2@gmail.com",
      "postCount": 0,
      "followCount": 0,
      "bookmarkOnOwnPostCount": 0,
      "commentOnOwnPostCount": 0,
      "fbLink": null,
      "instagramLink": null,
      "avatarLink": "https://lh3.googleusercontent.com/a/AATXAJwep-n67Wo_25OpuJb5x2jIzRMz8tj7uCEfGMR2=s96-c",
      "about": null,
      "occupation": null
    },
    "commentDate": "2021-05-11 15:23",
    "content": "&lt;p&gt;This is new content for my comment&lt;/p&gt;",
    "likeCount": 0,
    "liked": false,
    "myComment": true
  }
]
```

* **Error Response:**

    * **Code**: `404 NOT_FOUND` - không tìm bài viết

## Thêm một comment

----
Thêm 1 comment vào bài viết

* **URL**: `/comment/post/{post_id}`

* **Method:** `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name       | Type       | Description            |
  | ---------- |:------:    | ------------           |
  | `content`  | `string`   | Nội dung của comment   |

* **Success Response:**

    * **Code:** `200 OK` - Kèm thông tin của Comment vừa được thêm
  
    * **Example**:
    
```json5
{
  "commentId": 14,
  "commenter": {
    "accountId": 4,
    "name": "Trinhdvt2",
    "email": "trinhdvt2@gmail.com",
    "postCount": 0,
    "followCount": 0,
    "bookmarkOnOwnPostCount": 0,
    "commentOnOwnPostCount": 0,
    "fbLink": null,
    "instagramLink": null,
    "avatarLink": "https://lh3.googleusercontent.com/a/AATXAJwep-n67Wo_25OpuJb5x2jIzRMz8tj7uCEfGMR2=s96-c",
    "about": null,
    "occupation": null
  },
  "commentDate": "2021-05-12 13:40",
  "content": "&lt;p&gt;Hello my friend&lt;/p&gt;",
  "likeCount": 0,
  "liked": false,
  "myComment": true
}
```
    
* **Error Response:**

    * **Code**: `422 UNPROCESSABLE_ENTITY` - bài viết không tồn tại

    * **Code**: `403 FORBIDDEN` - chưa đăng nhập

## Sửa một comment

----
Sửa nội dung của một comment

* **URL**: `/comment/{comment_id}`

* **Method:** `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name        | Type       | Description  |
  | ----------  |:------:    | ------------ |
  | `content`   | `string`   | Nội dung mới |

* **Response:** Giống y như phần trên

## Xoá một comment

----
Xoá một comment

* **URL**: `/comment/{comment_id}`

* **Method:** `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `200 OK` - Xoá thành công kèm theo số lượng comment của bài viết hiện tại
  
* **Error Response:**

    * **Code**: `422 UNPROCESSABLE_ENTITY` - comment không tồn tại

    * **Code**: `403 FORBIDDEN` - chưa đăng nhập / không sở hữu

## Like / Unlike comment

----
Like / Unlike comment, nếu đã like thì sẽ trở thành unlike và ngược lại

* **URL**: `/comment/like/{comment_id}`

* **Method:** `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `200 OK` - Kèm theo số lượng like của comment vừa rồi
  
* **Error Response:**

    * **Code**: `422 UNPROCESSABLE_ENTITY` - comment không tồn tại

    * **Code**: `403 FORBIDDEN` - Chưa đăng nhập / không sở hữu