# `User` API Document

## Lấy ra thông tin chi tiết của một User (theo `id` và `email`)

----
Trả về thông tin chi tiết của một User

* **URL**: `/user/{account_id}` hoặc `/user?email={user_email}`

* **Method:** `GET`

* **Success Response:**

    - **Code:** 200 
      
      **Content:** thông tin chi tiết về User 

    * **Example:** `GET /user/1` hoặc `GET /user?email=trinhvideo123@gmail.com`
    
```json5
{
  "accountId": 1,
  "name": "Do Van Trinh",
  "email": "trinhvideo123@gmail.com",
  "postCount": 25, // số lượng bài viết đã viết
  "followCount": 2, // số lượng người theo dõi
  "bookmarkOnOwnPostCount": 2, // tổng số bookmark trên tất cả bài viết
  "commentOnOwnPostCount": 3,  // tổng số bình luận trên tất cả bài viết
  "fbLink": null,
  "instagramLink": null,
  "avatarLink": "https://lh3.googleusercontent.com/a/AATXAJwep-n67Wo_25OpuJb5x2jIzRMz8tj7uCEfGMR2=s96-c",
  "about": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đi caffe các kiểu",
  "occupation": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch"
}
```

* **Error Response:**

    * **Code**: 404 NOT_FOUND - user không tồn tại

## Lấy ra các bài viết của một User

----
Danh sách các bài viết của User (chỉ gồm các thông tin cơ bản như ở trang chủ) 

* **URL**: `/user/posts/{account_id}`

* **Method:** `GET`

* **Request Params**

  | Name                | Type       | Description                  | Default   |
  | ----------          |:------:    | ------------                 | :-------: |
  | `page <optional>`   | `int >= 0` | Thứ tự trang                 | 0         |
  | `size <optional>`   | `int >= 1` | Số lượng bài viết muốn lấy   | 5         |

* **Success Response:**

    * **Code:** 200 
      
      **Content:** danh sách các bài viết y chang như bên [Post API](Post_API.md#Lấy-ra-danh-sách-các-bài-viết-nổi-bật)
    
    * **Example**:
       
* **Error Response:**

    * **Code**: 404 NOT_FOUND - User không tồn tại

  
## Follow / Unfollow 1 người nào đó

----
Follow / Unfollow 1 người khác, nếu đã follow thì sẽ trở thành unfollow và ngược lại

* **URL**: `/user/follow/{account_id}`

* **Method:** `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Response:** 

  * **Code**: 200 OK
  
  * **Content**: số lượng người theo dõi của người vừa theo dõi

* **Error Response:**

  * **Code**: 422 UNPROCESSABLE_ENTITY - người muốn follow không tồn tại

  * **Code**: 403 FORBIDDEN - chưa đăng nhập
