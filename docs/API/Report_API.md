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
  "reportId": 2,
  "reporter": { // người đã report
    "accountId": 4,
    "name": "Trinhdvt2",
    "email": "trinhdvt2@gmail.com",
    "postCount": 0,
    "followCount": 0,
    "bookmarkOnOwnPostCount": 0,
    "commentOnOwnPostCount": 0,
    "avatarLink": "https://lh3.googleusercontent.com/a/AATXAJwep-n67Wo_25OpuJb5x2jIzRMz8tj7uCEfGMR2=s96-c",
    "about": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đi caffe các kiểu",
    "occupation": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đ",
    "role": "ROLE_ADMIN"
  },
  "postOwner": { // người sở hữu bài viết bị report
    "accountId": 1,
    "name": "Do Van Trinh",
    "email": "trinhvideo123@gmail.com",
    "postCount": 0,
    "followCount": 0,
    "bookmarkOnOwnPostCount": 0,
    "commentOnOwnPostCount": 0,
    "avatarLink": "https://lh3.googleusercontent.com/a/AATXAJwep-n67Wo_25OpuJb5x2jIzRMz8tj7uCEfGMR2=s96-c",
    "about": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đi caffe các kiểu",
    "occupation": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đ",
    "role": "ROLE_ADMIN"
  },
  "reportPostId": 1, // id của bài viết bị report
  "reportContent": "Bài viết có sử dụng hình ảnh có bản quyền, làm ơn xoá nó đi hộ cái",
  "reportDate": "2021-05-14 07:22",
  "solved": false // nếu là true thì có trả về thêm `decision`
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

  | Name        | Type    | Description                 |
  | -------     |:------: | ------------                |
  | `decision`  | `string`| Quyết định đã đưa ra        |

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

  | Name        | Type    | Description     |
  | -------     |:------: | ------------    |
  | `postId`    | `string`| Id của bài viết |
  | `content`   | `string`| Nội dung báo cáo|

* **Response:**

  * **Success Response:**

    * **Code:** `202 ACCEPTED` - Báo cáo thành công

  * **Error Response:**

    * **Code**: `404 NOT_FOUND` - Bài viết muốn báo cáo không tồn tại

    * **Code**: `403 FORBIDDEN` - Chưa đăng nhập