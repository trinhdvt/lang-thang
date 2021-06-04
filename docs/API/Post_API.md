# `Post (entity)` API Document

## Table of contents

* [Lấy danh sách các bài viết theo ngày đăng](#Lấy-ra-danh-sách-các-bài-viết-mới-nhất)
* [Lấy ra danh sách các bài viết nổi bật](#Lấy-ra-danh-sách-các-bài-viết-nổi-bật)
* [Lấy danh sách các bài viết theo từ khóa](#Tìm-kiếm-các-bài-viết-theo-tiêu-đề-và-nội-dung)
* [Lấy thông tin chi tiết một bài viết bằng id](#Lấy-thông-tin-chi-tiết-của-một-bài-viết-bằng-id)
* [Lấy thông tin chi tiết một bài viết bằng slug](#Lấy-thông-tin-chi-tiết-của-một-bài-viết-bằng-slug)
* [Đăng tải bài viết](#Đăng-tải-một-bài-viết-mới)
* [Lấy nội dung của bài viết hoặc bản nháp bằng slug (cho mục đích sửa)](#Lấy-nội-dung-của-bài-viết-hoặc-bản-nháp-bằng-slug)
* [Sửa bài viết hoặc chuyển bản nháp thành bài viết](#Sửa-bài-viết-hoặc-chuyển-bản-nháp-thành-bài-viết)
* [Xóa bài viết / bản nháp](#Xóa-bài-viết-hoặc-bản-nháp)
* [Thêm một bản nháp](#thêm-bản-nháp-mới)
* [Lấy ra một bản nháp](#Lấy-ra-một-bản-nháp)
* [Sửa bản nháp hoặc chuyển bài viết thành bản nháp](#Sửa-bản-nháp-hoặc-chuyển-bài-viết-thành-bản-nháp)

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
  "owner": false,
  "categories": [
    {
      "categoryId": 3,
      "categoryName": "Du lịch Sài Gòn",
      "postCount": 1
    },
    {
      "categoryId": 2,
      "categoryName": "Du lịch văn minh",
      "postCount": 1
    }
  ]
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

## Lấy nội dung của bài viết hoặc bản nháp bằng slug

----
Lấy ra nội dung của một bài viết hoặc bản nháp bằng `slug` cho mục đích sửa nội dung

* **URL**: `/post/{slug}/edit`

* **Method:**: `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

  * **Code:** `200 OK` - Kèm theo nội dung cơ bản của bài viết hoặc bản nháp

  * **Example**:

```json5
{
  "postId": "12",
  "title": "abcxyz",
  "content": "abcxyz",
  "postThumbnail": "http://abc.xyz.jpg",
  "..."
}
```

* **Error Response:**

  * **Code:** `404 NOT_FOUND` - Không tìm thấy

  * **Code:** `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / không sở hữu

## Đăng tải một bài viết mới

----
Đăng tải một bài viết mới

* **URL**: `/post`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                                |
| ----------        |:------:  | ------------                               |
| `title`           | `string` | Tiêu đề bài viết (max = 200 kí tự)         |
| `content`         | `string` | Nội dung bài viết                          |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết (max = 250) |
| `categories`      | `array`  | Mảng chứa các `category_id` (max = 5)      |

* **Success Response:**

    * **Code:** `200 OK` - Kèm theo `slug` của bài viết vừa đăng

    * **Example**:
  
```json5
{
  "slug": "abc-xyz-qwe"
}
```

* **Error Response:**

    * **Code:** `400 BAD REQUEST` - Param của request không hợp lệ

    * **Code:** `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / public một bài viết từ một bản nháp không do mình sở hữu

## Sửa bài viết hoặc chuyển bản nháp thành bài viết

----
Sửa một bài viết đã công khai hoặc chuyển bản nháp thành bài đăng công khai

* **URL**: `/post/{id}`

* **Method:**: `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                           |
| ----------        |:------:  | ------------                          |
| `title`           | `string` | Tên bài viết                          |
| `content`         | `string` | Nội dung bài viết                     |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết        |
| `categories`      | `array`  | Mảng chứa các `category_id` (max = 5) |

* **Success Response:**

    * **Code:** `200 OK` - Sửa thành công, kèm theo `slug` mới
  
    * **Example**:
  
```json5
{
  "slug": "abc-xyz-wer"
}
```

* **Error Response:**

    * **Code:** `400 BAD_REQUEST` - Sai tham số

    * **Code:** `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / không sở hữu
  
    * **Code:** `404 NOT_FOUND` - Không tồn tại

## Xóa bài viết hoặc bản nháp

----
Xoá một bài viết hoặc bản nháp do mình sở hữu. Riêng ADMIN có quyền xoá bài viết của người dùng

* **URL**: `/post/{id}`

* **Method:**: `DELETE`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `204 NO_CONTENT` - Xoá thành công

* **Error Response:**

    * **Code:** `404 NOT_FOUND` - Bài viết / bài nháp không tồn tại

    * **Code:** `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / không sở hữu

## Thêm bản nháp mới

----
Thêm một bản nháp mới

* **URL**: `/draft`

* **Method:**: `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                           |
| ----------        |:------:  | ------------                          |
| `title`           | `string` | Tên bài viết                          |
| `content`         | `string` | Nội dung bài viết                     |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết        |
| `categories`      | `array`  | Mảng chứa các `category_id` (max = 5) |

* **Success Response:**

    * **Code:** `200 OK` - Lưu thành công
    
* **Error Response:**
      
    * **Code:** `400 BAD_REQUEST` - Tham số không hợp lệ

    * **Code:** `403 FORBIDDEN` - Chưa đăng nhập

## Lấy ra một bản nháp

----
Lấy ra một bản nháp bằng id

* **URL**: `/draft/{id}`

* **Method:**: `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `200 OK` - Kèm theo thông tin của bản nháp
    
    * **Example**:
    
```json5
    
{
  "postId": 28,
  "title": "Mục sở thị hòn đảo của những người khổng lồ, 1m8 vẫn bị coi là người lùn",
  "publishedDate": "2021-05-10 06:26",
  "postThumbnail": "https://xxx.yyy.zzz.jpg",
  "categories": []
}

```

* **Error Response:**

    * **Code:** `404 NOT_FOUND` - Bản nháp không tồn tại
    
    * **Code:** `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / không sở hữu

## Sửa bản nháp hoặc chuyển bài viết thành bản nháp

----
Sửa bản nháp đã tồn tại hoặc chuyển một bài viết về dạng bản nháp (ẩn đi)

* **URL**: `/draft/{id}`

* **Method:**: `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

| Name              | Type     | Description                           |
| ----------        |:------:  | ------------                          |
| `title`           | `string` | Tên bài viết                          |
| `content`         | `string` | Nội dung bài viết                     |
| `postThumbnail`   | `string` | Link ảnh đại diện cho bài viết        |
| `categories`      | `array`  | Mảng chứa các `category_id` (max = 5) |

* **Success Response:**

    * **Code:** `204 NO_COTENT` - Sửa thành công

* **Error Response:**

    * **Cdoe:** `404 NOT_FOUND` - Bản nháp không tồn tại

    * **Code:** `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / không sở hữu
