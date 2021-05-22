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

  | Name         | Type       | Description                     | Default |
  | ----------   |:------:    | ------------                    | ------- |
  | `size`       | `int >= 0` | Số lượng bài viết muốn lấy về   | 10      |
  | `page`       | `int >= 0` | Index của trang                 | 0       |

* **Success Response:**

    * **Code:** `200 OK`
      
      **Content:** 1 danh sách các bài kèm theo các thông tin cơ bản

    * **Example:** `GET /post?page=0&size=3` hoặc `GET /post`
  
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

  | Name     | Type       | Description                                                    | Default |
  | -------- |:------:    | ------------                                                   | ------- |
  | `prop`   | `string`   | Tiêu chí để sắp xếp (hiện tại mới có `comment` và `bookmark`   |         |
  | `page`   | `int >= 1` | Index của page                                                 | 0       |
  | `size`   | `int >= 1` | Số lượng bài viết trả về                                       | 10      | 

* **Success Response:**

    - **Code:** `200 OK`
      
      **Content:** 1 danh sách các bài kèm theo các thông tin cơ bản
  
      **Lưu ý**: Với những bài viết có số lượng `bookmark` hoặc `comment` bằng `0` thì sẽ không được trả về 

    * **Example:** `GET /post?prop=bookmark` hoặc `GET /post?prop=bookmark&size=5`

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

    * **Code**: `422 UNPROCESSABLE_ENTITY`
      
    * **Content**: Không hỗ trợ với thuộc tính này

## Tìm kiếm các bài viết theo tiêu đề và nội dung

----
Trả về 1 danh sách các bài viết với tiêu đề hoặc nội dung có liên quan tới từ khóa

* **URL**: `/post`

* **Method:** `GET`

* **Request Params**

  | Name        | Type       | Description                      | Default |
  | ----------  |:------:    | ------------                     | ------- |
  | `keyword`   | `string`   | Từ khóa cần tìm                  |         |
  | `size`      | `int >= 0` | Số lượng bài viết muốn lấy về    | 10      |
  | `page`      | `int >= 0` | Index của trang                  | 0       |

* **Response:** Giống y như phần trên

## Lấy thông tin chi tiết của một bài viết bằng id

----
Trả về thông tin chi tiết của một bài viết

* **URL**: `/post/{id}`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại hoặc rỗng (nếu như chưa đăng nhập)>`

* **Success Response:**

    * **Code:** `200 OK`
      
      **Content:** một loạt thông tin về bài viết
      
      **Note**: trong trường hợp người dùng đã đăng nhập (`token` trong `request_header` không rỗng) thì server sẽ xác định các giá trị trả về sau:
        - `owner = true` - bài viết hiện tại do `tôi` viết
        - `bookmarked = true` - `tôi` đã bookmark bài viết này
        - `myComment = true` - đây là comment của `tôi`
        - `liked = true` - `tôi` đã like comment này
        - nếu chưa đăng nhập thì mặc định là `false` hết
    
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
  "owner": false
}
```

* **Error Response:**
  
    * **Code:** `404 NOT FOUND` - Không tồn tại

## Lấy thông tin chi tiết của một bài viết bằng slug

----
Trả về thông tin chi tiết của một bài viết

* **URL**: `/post`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại hoặc rỗng (nếu như chưa đăng nhập)>`

* **Request Param:**

  | Name     | Type       | Description         | Default |
  | -------- |:------:    | ------------        | ------- |
  | `slug`   | `string`   | `slug` của bài viết |         |

* **Response:** Xem lại phần trên

## Đăng tải một bài viết mới (public luôn, không phải nháp)

----
Đăng tải một bài viết mới

* **URL**: `/post`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                        |
| ----------        |:------:  | ------------                       |
| `title`           | `string` | Tên bài viết                       |
| `content`         | `string` | Nội dung bài viết                  |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết     |

* **Success Response:**

    * **Code:** `200 OK` - Kèm theo thông tin cơ bản của bài viết vừa đăng 
    
* **Error Response:**

    * **Code:** `400 BAD REQUEST` - Param của request không hợp lệ

    * **Code:** `401 FORBIDDEN` - Chưa đăng nhập / public một bài viết từ một bản nháp không do mình sở hữu

## Sửa bài viết

----
Sửa bài viết (bài của ai người đó sửa)

* **URL**: `/post/{id}`

* **Method:**: `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                    |
| ----------        |:------:  | ------------                   |
| `title`           | `string` | Tên bài viết                   |
| `content`         | `string` | Nội dung bài viết              |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết |

* **Success Response:**

    * **Code:** `202 ACCEPTED` - Sửa thành công

* **Error Response:**

    * **Code:** `403 FORBIDDEN` - Không có quyền (chưa đăng nhập / không sở hữu) 

## Xóa bài viết (thực ra là ẩn đi thôi)

----
Ẩn đi một bài viết (`admin` có quyền)

* **URL**: `/post/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `204 NO_CONTENT` - Xoá thành công

* **Error Response:**

    * **Code:** `404 NOT_FOUND` - Bài viết không tồn tại

    * **Code:** `403 FORBIDDEN` - Không có quyền (chưa đăng nhập / không sở hữu)

## Lưu bản nháp

----
Lưu một bản nháp mới

* **URL**: `/post/draft`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                        |
| ----------        |:------:  | ------------                       |
| `title`           | `string` | Tên bài viết                       |
| `content`         | `string` | Nội dung bài viết                  |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết     |

* **Success Response:**

    * **Code:** `202 ACCEPTED` - Lưu thành công
    
* **Error Response:**
  
    * **Code:** `403 FORBIDDEN` - Chưa đăng nhập

## Lấy ra một bản nháp

----
Lấy ra một bản nháp

* **URL**: `/post/draft/{id}`

* **Method:**: `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `202 ACCEPTED` - Kèm theo thông tin của bản nháp
    
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

    * **Code:** `404 NOT_FOUND` - Bản nháp không tồn tại
    
    * **Code:** `403 FORBIDDEN` - Không có quyền (chưa đăng nhập / không sở hữu)

## Sửa bản nháp

----
Sửa bản nháp của mình (của ai người đó sửa)

* **URL**: `/post/draft/{id}`

* **Method:**: `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                        |
| ----------        |:------:  | ------------                       |
| `title`           | `string` | Tên bài viết                       |
| `content`         | `string` | Nội dung bài viết                  |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết    |

* **Success Response:**

    * **Code:** `202 ACCEPTED` - Sửa thành công

* **Error Response:**

    * **Cdoe:** `404 NOT_FOUND` - Bản nháp không tồn tại

    * **Code:** `403 FORBIDDEN` - Không có quyền

## Xóa bản nháp (hiện tại là ẩn đi thôi, sau này là xóa luôn)

----
Xóa đi một bản nháp (của ai người đó xoá)

* **URL**: `/post/draft/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `204 NO_CONTENT` - Xoá thành công

* **Error Response:**

    * **Code:** `404 NOT_FOUND` - Bản nháp không tồn tại
    
    * **Code:** `403 FORBIDDEN` - Không có quyền (chưa đăng nhập / không sở hữu)