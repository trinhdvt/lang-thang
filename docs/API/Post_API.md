# `Post (entity)` API Document

## Table of contents

* [Lấy danh sách các bài viết theo ngày đăng](#Lấy-ra-danh-sách-các-bài-viết-mới-nhất)
* [Lấy danh sách các bài viết theo từ khóa](#Tìm-kiếm-các-bài-viết-theo-tiêu-đề-và-nội-dung)
* [Lấy thông tin chi tiết một bài viết](#Lấy-thông-tin-chi-tiết-của-một-bài-viết)
* [Đăng tải bài viết](#Đăng-tải-một-bài-viết-mới-(public-luôn,-không-phải-nháp))
* [Sửa bài viết](#sửa-bài-viết)
* [Xóa (ẩn) bài viết](#Xóa-bài-viết-(thực-ra-là-ẩn-đi-thôi))
* [Thêm một bản nháp](#lưu-bản-nháp)
* [Lấy một bản nháp](#Lấy-ra-một-bản-nháp)
* [Sửa bản nháp](#Sửa-bản-nháp)
* [Xóa bản nháp](#Xóa-bản-nháp-(hiện-tại-là-ẩn-đi-thôi,-sau-này-là-xóa-luôn))

## Lấy ra danh sách các bài viết mới nhất

----
Trả về 1 danh sách các bài viết được sắp xếp theo ngày đăng - `publishedDate`

* **URL**: `/post/`

* **Method:** `GET`

* **Request Params**

  | Name         | Type       | Description                     |
  | ----------   |:------:    | ------------                    |
  | `page`       | `int >= 0` | Số lượng bài viết muốn lấy về   |
  | `size`       | `int >= 0` | Index của trang                 |

* **Success Response:**

    - **Code:** 200 <br />
      **Content:** 1 danh sách các bài kèm theo các thông tin cơ bản

    * **Example:** `GET /post?page=0&size=3`
  
    ```json5
    [{
        "postId": 28,
        "title": "Mục sở thị hòn đảo của những người khổng lồ, 1m8 vẫn bị coi là người lùn",
        "publishedDate": "2021-05-10 06:26",
        "postThumbnail": "https://xxx.yyy.zzz.jpg",
        "bookmarkedCount": 0,
        "commentCount": 0,
    },
    {
        "postId": 26,
        "title": "Mục sở thị hòn đảo của những người khổng lồ, 1m8 vẫn bị coi là người lùn",
        "publishedDate": "2021-05-09 15:51",
        "postThumbnail": "https://xxx.yyy.zzz.jpg",
        "bookmarkedCount": 0,
        "commentCount": 0,
    },
    {
        "postId": 1,
        "title": "Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
        "publishedDate": "2021-05-06 14:57",
        "postThumbnail": "https://xxx.yyy.zzz.jpg",
        "bookmarkedCount": 1,
        "commentCount": 4,
    }]
  ```

* **Error Response:** `Code != 200` là có lỗi

## Tìm kiếm các bài viết theo tiêu đề và nội dung

----
Trả về 1 danh sách các bài viết với tiêu đề hoặc nội dung có liên quan tới từ khóa

* **URL**: `/post/`

* **Method:** `GET`

* **Request Params**

  | Name        | Type       | Description                      |
  | ----------  |:------:    | ------------                     |
  | `keyword`   | `string`   | Từ khóa cần tìm                  |
  | `page`      | `int >= 0` | Số lượng bài viết muốn lấy về    |
  | `size`      | `int >= 0` | Index của trang                  |

* **Response:** Giống y như phần trên

## Lấy thông tin chi tiết của một bài viết

----
Trả về thông tin chi tiết của một bài viết

* **URL**: `/post/{id}`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại hoặc rỗng (nếu như chưa đăng nhập)>`

* **Success Response:**

    * **Code:** 200
      
      **Content:** một loạt thông tin về bài viết
      
      **Note**: trong trường hợp người dùng đã đăng nhập (`token` trong `request_header` không rỗng) thì server sẽ xác định các giá trị trả về sau:
        - `owner = true` - bài viết hiện tại do `tôi` viết
        - `bookmarked = true` - `tôi` đã bookmark bài viết này
        - `myComment = true` - đây là comment của `tôi`
        - `liked = true` - `tôi` đã like comment này
        - nếu chưa đăng nhập thì mặc định là false hết
    
    * **Example**: `GET /post/1`
  
```json5
{
  "postId": 1,
  "title": "Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
  "publishedDate": "2021-05-06 14:57",
  "postThumbnail": "https://xxx.yyy.zzz.jpg",
  "author": {
    "accountId": 1,
    "name": "Do Van Trinh",
    "email": "trinhvideo123@gmail.com",
    "postCount": 25,
    "followCount": 1,
    "avatarLink": null,
    "about": null,
    "occupation": null
  },
  "content": "abcxyz",
  "bookmarked": true,
  "bookmarkedCount": 1,
  "commentCount": 2,
  "comments": [
    {
      "commentId": 4,
      "commenter": {
        "accountId": 1,
        "name": "Do Van Trinh",
        "email": "trinhvideo123@gmail.com",
        "avatarLink": null
      },
      "commentDate": "2021-05-06 15:25",
      "content": "Đẹp ghê á bạn ơi, cho mình xin nick fb với",
      "likeCount": 1,
      "myComment": false,
      "liked": false
    },
    {
      "commentId": 5,
      "commenter": {
        "accountId": 4,
        "name": "Trinhdvt2",
        "email": "trinhdvt2@gmail.com",
        "avatarLink": null,
      },
      "commentDate": "2021-05-06 15:26",
      "content": "きれい！どこですか？教えてください。",
      "likeCount": 1,
      "myComment": false,
      "liked": false
    }
  ],
  "owner": false
}
```

* **Error Response:** `Code != 200` là có lỗi hết
  
    * **Code:** 404 NOT FOUND 

    
## Đăng tải một bài viết mới (public luôn, không phải nháp)

----
Đăng tải một bài viết mới

* **URL**: `/post/`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:**

| Name              | Type     | Description                        |
| ----------        |:------:  | ------------                       |
| `title`           | `string` | Tên bài viết                       |
| `content`         | `string` | Nội dung bài viết                  |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết     |

* **Success Response:**

    * **Code:** 201 CREATED 
      
      **Content:** `Location: /post/{saved_post_id}` được gắn vào trong `request_header`
      
* **Error Response:**

    * **Code:** 401 UNAUTHORIZED 
      
      **Lý do:** Public một bài viết từ một bản nháp không do mình sở hữu

    * **Code:** 403 FORBIDDEN 
      
      **Lý do:** Chưa đăng nhập

## Sửa bài viết

----
Sửa bài viết (`admin` không có quyền sửa)

* **URL**: `/post/{id}`

* **Method:**: `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:**

| Name              | Type     | Description                    |
| ----------        |:------:  | ------------                   |
| `title`           | `string` | Tên bài viết                   |
| `content`         | `string` | Nội dung bài viết              |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết |

* **Success Response:**

    * **Code:** 202 ACCEPTED

* **Error Response:**

    * **Code:** 401 UNAUTHORIZED

      **Lý do:** Bài viết không thuộc quyền sở hữu hoặc không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Chưa đăng nhập
    
    
## Xóa bài viết (thực ra là ẩn đi thôi)

----
Ẩn đi một bài viết (`admin` có quyền)

* **URL**: `/post/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** 204 NO_CONTENT

* **Error Response:**

    * **Code:** 401 UNAUTHORIZED

      **Lý do:** Bài viết không thuộc quyền sở hữu hoặc không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Chưa đăng nhập

## Lưu bản nháp

----
Lưu một bản nháp mới

* **URL**: `/post/draft`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:**

| Name         | Type     | Description          |
| ----------   |:------:  | ------------         |
| `title`      | `string` | Tên bài viết |
| `content`      | `string` | Nội dung bài viết |
| `postThumbnail`   | `string` |  Link ảnh đại diện cho bài viết           |

* **Success Response:**

    * **Code:** 202 ACCEPTED


* **Error Response:**
  
    * **Code:** 403 FORBIDDEN

      **Lý do:** Chưa đăng nhập

## Lấy ra một bản nháp

----
Lấy ra một bản nháp

* **URL**: `/post/draft/{id}`

* **Method:**: `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** 202 ACCEPTED
    
    * **Example**:
    
```json5
    
{
  "postId": 28,
  "title": "Mục sở thị hòn đảo của những người khổng lồ, 1m8 vẫn bị coi là người lùn",
  "publishedDate": "2021-05-10 06:26",
  "postThumbnail": "https://xxx.yyy.zzz.jpg",
}

```

* **Error Response:**
  
    * **Code:** 401 UNAUTHORIZED  

      **Lý do:** Bản nháp không thuộc quyền sở hữu
    
    * **Code:** 403 FORBIDDEN

      **Lý do:** Chưa đăng nhập

    * **Code:** 404 NOTFOUND

      **Lý do:** Không tồn tại

## Sửa bản nháp

----
Sửa bản nháp của mình (`admin` không có quyền sửa)

* **URL**: `/post/draft/{id}`

* **Method:**: `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:**

| Name              | Type     | Description                        |
| ----------        |:------:  | ------------                       |
| `title`           | `string` | Tên bài viết                       |
| `content`         | `string` | Nội dung bài viết                  |
| `postThumbnail`   | `string` |  Link ảnh đại diện cho bài viết    |

* **Success Response:**

    * **Code:** 202 ACCEPTED

* **Error Response:**

    * **Code:** 401 UNAUTHORIZED

      **Lý do:** Bản nháp không thuộc quyền sở hữu hoặc không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Chưa đăng nhập

    
## Xóa bản nháp (hiện tại là ẩn đi thôi, sau này là xóa luôn)

----
Xóa đi một bản nháp (`admin` không có quyền)

* **URL**: `/post/draft/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** 204 NO_CONTENT

* **Error Response:**

    * **Code:** 401 UNAUTHORIZED

      **Lý do:** Bản nháp không thuộc quyền sở hữu hoặc không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Chưa đăng nhập
    

