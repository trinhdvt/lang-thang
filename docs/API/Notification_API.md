# `Notification` API Document

## Lấy ra danh sách các thông báo

----
Trả về 1 danh sách các thông báo (mặc định sắp xếp theo thời gian và trạng thái đã được hay chưa)

* **URL**: `/notifications`

* **Method:** `GET`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Params**

  | Name    | Type    | Description                 | Default   |
  | ------- |:------: | ------------                | :-------: |
  | `page`  | `int`   | Số thứ tự trang             | 0         |
  | `size`  | `int`   | Số lượng thông báo muốn lấy | 10        |
  
* **Success Response:**

    * **Code:** `200 OK` - Kèm 1 danh sách các thông báo

    * **Example:** `GET /notifications`
    
```json5
[
  {
    "notificationId": 2,
    "destEmail": "trinhvideo123@gmail.com", // người nhận thông báo
    "sourceAccount": { // người tạo ra thông báo
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
    "destPost": { // bài viết liên quan đến thông báo
      "postId": 1,
      "title": "Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
      "slug": "Cam-nhan-mot-xu-Hue-duoi-goc-nhin-cuc-chat-cua-co-nang-Kim-Ngan-dam-me-xe-dich-1620666137954",
      "bookmarkedCount": 0,
      "commentCount": 0,
      "bookmarked": false,
      "owner": false
    },
    "content": "Day la 1 notify co post_id",
    "notifyDate": "2021-04-25 15:26",
    "seen": false,
    "notificationType": null
  },
  {
    "notificationId": 12,
    "destEmail": "trinhvideo123@gmail.com",
    "sourceAccount": {
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
    "destPost": {
      "postId": 1,
      "title": "Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
      "slug": "Cam-nhan-mot-xu-Hue-duoi-goc-nhin-cuc-chat-cua-co-nang-Kim-Ngan-dam-me-xe-dich-1620666137954",
      "bookmarkedCount": 0,
      "commentCount": 0,
      "bookmarked": false,
      "owner": false
    },
    "content": "Do Van Trinh đã thích bình luận của bạn trong bài viết Cảm nhận một xứ Huế dưới góc nhìn “cực chất” của cô nàng Kim Ngân đam mê xê dịch",
    "notifyDate": "2021-05-15 04:33",
    "seen": false,
    "notificationType": null
  }
]
```

* **Error Response:**

  * **Code**: `403 FORBIDDEN` - Chưa đăng nhập

## Lấy ra các thông báo chưa xem

----
Lấy ra danh sách các thông báo chưa xem

* **URL**: `/notifications/unseen`

* **Method:** `GET`

* **Response**: Giống như phần trên

## Đánh dấu một thông báo là đã xem

----
Đánh dấu một thông báo là đã xem

* **URL**: `/notifications/{notification_id}/seen`

* **Method:** `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Response:**

  * **Success Response:**

    * **Code:** `204 NO_CONTENT` - Đã đánh dấu là đã xem

  * **Error Response:**
  
    * **Code**: `403 FORBIDDEN / 401 UNAUTHORIZED` - Chưa đăng nhập / Không sở hữu
      
    * **Code**: `404 NOT_FOUND` - Thông báo không tồn tại