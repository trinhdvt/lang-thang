# `Post (entity)` API Document

## Table of contents

* [Lấy danh sách các bài viết theo ngày đăng](#Lấy-ra-danh-sách-các-bài-viết-mới-nhất)
* [Lấy ra danh sách các bài viết nổi bật](#Lấy-ra-danh-sách-các-bài-viết-nổi-bật)
* [Lấy danh sách các bài viết theo từ khóa](#Tìm-kiếm-các-bài-viết-theo-tiêu-đề-và-nội-dung)
* [Lấy thông tin chi tiết một bài viết bằng id](#Lấy-thông-tin-chi-tiết-của-một-bài-viết-bằng-id)
* [Lấy thông tin chi tiết một bài viết bằng slug](#Lấy-thông-tin-chi-tiết-của-một-bài-viết-bằng-slug)
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

* **URL**: `/post`

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
        "slug": "Muc-so-thi-hon-dao-cua-nhung-nguoi-khong-lo-1m8-van-bi-coi-la-nguoi-lun-1620666139010"
    },
    {
        "postId": 26,
        "title": "Mục sở thị hòn đảo của những người khổng lồ, 1m8 vẫn bị coi là người lùn",
        "publishedDate": "2021-05-09 15:51",
        "postThumbnail": "https://xxx.yyy.zzz.jpg",
        "bookmarkedCount": 0,
        "commentCount": 0,
        "slug": "Muc-so-thi-hon-dao-cua-nhung-nguoi-khong-lo-1m8-van-bi-coi-la-nguoi-lun-1620666139010"
    },
    {
        "postId": 1,
        "title": "Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
        "publishedDate": "2021-05-06 14:57",
        "postThumbnail": "https://xxx.yyy.zzz.jpg",
        "bookmarkedCount": 1,
        "commentCount": 4,
        "slug": "Muc-so-thi-hon-dao-cua-nhung-nguoi-khong-lo-1m8-van-bi-coi-la-nguoi-lun-1620666139010"
    }]
  ```

* **Error Response:** `Code != 200` là có lỗi

## Lấy ra danh sách các bài viết nổi bật

----
Trả về 1 danh sách các bài viết được sắp xếp dựa theo số lượng `comment` hoặc số lượng `bookmark`

* **URL**: `/post`

* **Method:** `GET`

* **Request Params**

  | Name                | Type       | Description                                                    |
  | ----------          |:------:    | ------------                                                   |
  | `sort`              | `string`   | Tiêu chí để sắp xếp (hiện tại mới có `comment` và `bookmark`   |
  | `size <optional>`   | `int >= 1` | Số lượng bài viết trả về (mặc định là 5)                       |

* **Success Response:**

    - **Code:** 200 
      
      **Content:** 1 danh sách các bài kèm theo các thông tin cơ bản

    * **Example:** `GET /post?sort=bookmark` hoặc `GET /post?sort=bookmark&size=5`

    ```json5
    [{
        "postId": 1,
        "title": "Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
        "slug": "Cam-nhan-mot-xu-Hue-duoi-goc-nhin-cuc-chat-cua-co-nang-Kim-Ngan-dam-me-xe-dich-1620666137954",
        "publishedDate": "2021-05-06 14:57",
        "postThumbnail": "https://dulichvietnam.com.vn/vnt_upload/news/05_2021/thumbs/kim_ngan__10.jpg",
        "bookmarkedCount": 2,
        "commentCount": 4,
    },
    {
        "postId": 2,
        "title": "&#039;Ghim&#039; ngay 8 homestay ở miền Trung giá rẻ dịp nghỉ lễ 30/4",
        "slug": "039-Ghim-039-ngay-8-homestay-o-mien-Trung-gia-re-dip-nghi-le-30-4-1620666138057",
        "publishedDate": "2021-05-06 14:57",
        "postThumbnail": "https://dulichvietnam.com.vn/vnt_upload/news/04_2021/thumbs/Homestay_o_mien_Trung_171_1.jpg",
        "bookmarkedCount": 1,
        "commentCount": 1,
    },
    {
        "postId": 3,
        "title": "Bỏ túi 3 điểm đến ở Mũi Né để có ảnh check in &#039;mãn nhãn&#039; như cặp đôi Đồng Nai",
        "slug": "Bo-tui-3-diem-den-o-Mui-Ne-de-co-anh-check-in-039-man-nhan-039-nhu-cap-doi-ong-Nai-1620666138106",
        "publishedDate": "2021-05-06 14:57",
        "postThumbnail": "https://dulichvietnam.com.vn/vnt_upload/news/10_2020/thumbs/du_lich_mui_ne_3_1_1.jpg",
        "bookmarkedCount": 1,
        "commentCount": 0,
    }]
  ```

* **Error Response:**

    * **Code**: 422 UNPROCESSABLE_ENTITY
    * **Content**: Không hỗ trợ với thuộc tính này

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

## Lấy thông tin chi tiết của một bài viết bằng id

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
  "slug": "Muc-so-thi-hon-dao-cua-nhung-nguoi-khong-lo-1m8-van-bi-coi-la-nguoi-lun-1620666139010",
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

## Lấy thông tin chi tiết của một bài viết bằng slug

----
Trả về thông tin chi tiết của một bài viết

* **URL**: `/post/{slug}`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại hoặc rỗng (nếu như chưa đăng nhập)>`

* **Response:** Xem lại phần trên

## Đăng tải một bài viết mới (public luôn, không phải nháp)

----
Đăng tải một bài viết mới

* **URL**: `/post`

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

    * **Code:** 401 FORBIDDEN 
      
      **Lý do:** Chưa đăng nhập / public một bài viết từ một bản nháp không do mình sở hữu
    
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

    * **Code:** 403 FORBIDDEN

      **Lý do:** Không có quyền (chưa đăng nhập / không sở hữu)

## Xóa bài viết (thực ra là ẩn đi thôi)

----
Ẩn đi một bài viết (`admin` có quyền)

* **URL**: `/post/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** 204 NO_CONTENT

* **Error Response:**

    * **Code:** 404 NOT_FOUND

      **Lý do:** Bản nháp không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Không có quyền (chưa đăng nhập / không sở hữu)
    
## Lưu bản nháp

----
Lưu một bản nháp mới

* **URL**: `/post/draft`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:**

| Name              | Type     | Description                        |
| ----------        |:------:  | ------------                       |
| `title`           | `string` | Tên bài viết                       |
| `content`         | `string` | Nội dung bài viết                  |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết     |

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

    * **Code:** 404 NOT_FOUND

      **Lý do:** Bản nháp không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Không có quyền (chưa đăng nhập / không sở hữu)

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

    * **Code:** 403 FORBIDDEN

      **Lý do:** Không có quyền (chưa đăng nhập / không sở hữu)

    
## Xóa bản nháp (hiện tại là ẩn đi thôi, sau này là xóa luôn)

----
Xóa đi một bản nháp (`admin` không có quyền)

* **URL**: `/post/draft/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** 204 NO_CONTENT

* **Error Response:**

    * **Code:** 404 NOT_FOUND

      **Lý do:** Bản nháp không tồn tại

    * **Code:** 403 FORBIDDEN

      **Lý do:** Không có quyền (chưa đăng nhập / không sở hữu)
    

