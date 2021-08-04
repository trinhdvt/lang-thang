# `Report` API Document

## Lấy ra danh sách các báo cáo (dành cho `ADMIN`)

----
Trả về 1 danh sách các báo cáo (mặc định sắp xếp theo trạng thái và ngày báo cáo)

* **URL**: `/report`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Params**

  | Name    | Type    | Description                 | Default   |
  | ------- |:------: | ------------                | :-------: |
  | `page`  | `int`   | Số thứ tự trang             | 0         |
  | `size`  | `int`   | Số lượng báo cáo muốn lấy   | 10        |
  
* **Success Response:**

    * **Code:** `200 OK` - Kèm 1 danh sách các báo cáo

    * **Example:** `GET /report`
    
```json5
[
  {
    "reportId": 1,
    "reportPostId": 1,
    "reportContent": "Bài viết quá tệ, viết như đấm vào mồm vậy",
    "reportDate": "2021-05-14 07:21",
    "solved": false
  },
  {
    "reportId": 2,
    "reportPostId": 1,
    "reportContent": "Bài viết có sử dụng hình ảnh có bản quyền, làm ơn xoá nó đi hộ cái",
    "reportDate": "2021-05-14 07:22",
    "solved": false
  },
  {
    "reportId": 3,
    "reportPostId": 4,
    "reportContent": "Bài viết được copy từ facebook nhưng không ghi nguồn",
    "reportDate": "2021-05-14 07:22",
    "solved": false
  },
  {
    "reportId": 4,
    "reportPostId": 5,
    "reportContent": "Bài viết có sử dụng từ ngữ nhạy cảm",
    "reportDate": "2021-05-14 07:23",
    "solved": true,
    "decision": "Đã xoá"
  }
]
```

* **Error Response:**

  * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`

## Xem thông tin chi tiết một báo cáo (dành cho `ADMIN`)

----
Lấy ra thông tin chi tiết của một báo cáo

* **URL**: `/report/{report_id}`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

  * **Code:** `200 OK` - Kèm theo thông tin chi tiết về báo cáo

  * **Example:** `GET /report/2`
  
```json5
{
  "reportId": 4,
  "reporter": {
    "accountId": 5,
    "name": "Nguyễn Thanh Dũng",
    "email": "nguyenthanhdung@gmail.com",
    "postCount": 0,
    "followCount": 0,
    "bookmarkOnOwnPostCount": 0,
    "commentOnOwnPostCount": 0,
    "avatarLink": "https://langthang-user-photos.s3-ap-southeast-1.amazonaws.com/avatar2.png",
    "about": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đi caffe các kiểu",
    "role": "ROLE_ADMIN",
    "followed": false
  },
  "reportedPost": {
    "postId": 5,
    "slug": "Co-3-mon-ca-phe-Viet-Nam-ngon-nhat-khien-nhieu-nguoi-039-cuong-si-039-1620666138223"
  },
  "reportContent": "Bài viết có sử dụng từ ngữ nhạy cảm",
  "reportDate": "2021-05-14 07:23",
  "solved": true,
  "decision": "Đã xoá"
}
```

* **Error Response:**

  * **Code**: `404 NOT_FOUND` - Báo cáo không tồn tại
    
  * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`

## Đánh dấu một báo cáo là đã giải quyết (dành cho `ADMIN`)

----
Đánh dấu một báo cáo là đã giải quyết

* **URL**: `/report/{report_id}`

* **Method:** `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Params**

  | Name        | Type    | Description                             |
  | -------     |:------: | ------------                            |
  | `decision`  | `string`| Quyết định đã đưa ra (không được rỗng)  |

* **Response:**

  * **Success Response:**

    * **Code:** `200 OK` - Kèm theo thông tin cơ bản về báo cáo đã giải quyết (xem lại phần đầu tiên)

  * **Error Response:**
  
    * **Code**: `404 NOT_FOUND` - Báo cáo không tồn tại
      
    * **Code**: `403 FORBIDDEN` - Không phải `ADMIN`

## Báo cáo một bài viết (dành cho `User`)

----
Báo cáo một bài viết

* **URL**: `/report`

* **Method:** `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Params**

  | Name        | Type    | Description                        |
  | -------     |:------: | ------------                       |
  | `postId`    | `string`| Id của bài viết                    |
  | `content`   | `string`| Nội dung báo cáo (không được rỗng) |

* **Response:**

  * **Success Response:**

    * **Code:** `202 ACCEPTED` - Báo cáo thành công

  * **Error Response:**

    * **Code**: `404 NOT_FOUND` - Bài viết muốn báo cáo không tồn tại

    * **Code**: `403 FORBIDDEN` - Chưa đăng nhập
  
    * **Code**: `422 UNPROCESSABLE_ENTITY` - Không thể tự báo cáo bài viết của chính mình
  
