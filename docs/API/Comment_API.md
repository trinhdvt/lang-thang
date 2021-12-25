# `Comment` API Document

## Lấy ra danh sách các comment của một bài viết (bằng `id` hoặc `slug`)

----
Trả về 1 danh sách các comment của một bài viết (được sắp xếp tăng dần theo ngày comment)

* **URL**: `/comment/post/{post_id}` hoặc `/comment/post?slug={slug}`

* **Method:** `GET`

* **Header:** `Authorization: Bearer <access token> (optional)`

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
    "commentId": 634,
    "parentId": null,
    "commenter": {
      "accountId": 32,
      "name": "oanhkhongphaiadmin",
      "email": "loveya227@gmail.com",
      "postCount": 0,
      "followCount": 0,
      "bookmarkOnOwnPostCount": 0,
      "commentOnOwnPostCount": 0,
      "fbLink": "https:&#x2F;&#x2F;www.facebook.com&#x2F;",
      "instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
      "avatarLink": "https:&#x2F;&#x2F;dm6niys7elogn.cloudfront.net&#x2F;1628555664743_tumblr_ohxafbDzlJ1sdwnm3o1_1280.jpg",
      "about": "mình là kiều oanh sinh viên đại học bách khoa đà nẵng",
      "followed": false
    },
    "postId": 1,
    "commentDate": "2021-08-07 20:59",
    "content": "ok bạn",
    "likeCount": 0,
    "childComments": [
      {
        "commentId": 60,
        "parentId": 634,
        "commenter": {
          "accountId": 32,
          "name": "oanhkhongphaiadmin",
          "email": "loveya227@gmail.com",
          "postCount": 0,
          "followCount": 0,
          "bookmarkOnOwnPostCount": 0,
          "commentOnOwnPostCount": 0,
          "fbLink": "https:&#x2F;&#x2F;www.facebook.com&#x2F;",
          "instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
          "avatarLink": "https:&#x2F;&#x2F;dm6niys7elogn.cloudfront.net&#x2F;1628555664743_tumblr_ohxafbDzlJ1sdwnm3o1_1280.jpg",
          "about": "mình là kiều oanh sinh viên đại học bách khoa đà nẵng",
          "followed": false
        },
        "postId": 1,
        "commentDate": "2021-05-18 12:09",
        "content": "ma sao khong duoc nhi",
        "likeCount": 0,
        "childComments": null,
        "myComment": false,
        "liked": false
      },
      {
        "commentId": 75,
        "parentId": 634,
        "commenter": {
          "accountId": 32,
          "name": "oanhkhongphaiadmin",
          "email": "loveya227@gmail.com",
          "postCount": 0,
          "followCount": 0,
          "bookmarkOnOwnPostCount": 0,
          "commentOnOwnPostCount": 0,
          "fbLink": "https:&#x2F;&#x2F;www.facebook.com&#x2F;",
          "instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
          "avatarLink": "https:&#x2F;&#x2F;dm6niys7elogn.cloudfront.net&#x2F;1628555664743_tumblr_ohxafbDzlJ1sdwnm3o1_1280.jpg",
          "about": "mình là kiều oanh sinh viên đại học bách khoa đà nẵng",
          "followed": false
        },
        "postId": 1,
        "commentDate": "2021-05-18 12:10",
        "content": "?",
        "likeCount": 0,
        "childComments": null,
        "myComment": false,
        "liked": false
      }
    ],
    "myComment": false,
    "liked": false
  },
  {
    "commentId": 633,
    "parentId": null,
    "commenter": {
      "accountId": 6,
      "name": "Kiều Oanh",
      "email": "lecanhkieuoanh@gmail.com",
      "postCount": 0,
      "followCount": 0,
      "bookmarkOnOwnPostCount": 0,
      "commentOnOwnPostCount": 0,
      "fbLink": "",
      "instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
      "avatarLink": "https:&#x2F;&#x2F;dm6niys7elogn.cloudfront.net&#x2F;1629122789406_d13546-2815-8caac21513792f0006a2-0.jpg",
      "about": "Mình là kiều oanh học lớp 18TCLC- nhật trường đại học bách khoa đà nẽnggg",
      "role": "ROLE_ADMIN",
      "followed": false
    },
    "postId": 1,
    "commentDate": "2021-08-07 20:59",
    "content": "ok mình thấy bạn gòi nak",
    "likeCount": 0,
    "childComments": [
      {
        "commentId": 59,
        "parentId": 633,
        "commenter": {
          "accountId": 32,
          "name": "oanhkhongphaiadmin",
          "email": "loveya227@gmail.com",
          "postCount": 0,
          "followCount": 0,
          "bookmarkOnOwnPostCount": 0,
          "commentOnOwnPostCount": 0,
          "fbLink": "https:&#x2F;&#x2F;www.facebook.com&#x2F;",
          "instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
          "avatarLink": "https:&#x2F;&#x2F;dm6niys7elogn.cloudfront.net&#x2F;1628555664743_tumblr_ohxafbDzlJ1sdwnm3o1_1280.jpg",
          "about": "mình là kiều oanh sinh viên đại học bách khoa đà nẵng",
          "followed": false
        },
        "postId": 1,
        "commentDate": "2021-05-18 12:09",
        "content": "hey man",
        "likeCount": 0,
        "childComments": null,
        "myComment": false,
        "liked": false
      },
      {
        "commentId": 74,
        "parentId": 633,
        "commenter": {
          "accountId": 32,
          "name": "oanhkhongphaiadmin",
          "email": "loveya227@gmail.com",
          "postCount": 0,
          "followCount": 0,
          "bookmarkOnOwnPostCount": 0,
          "commentOnOwnPostCount": 0,
          "fbLink": "https:&#x2F;&#x2F;www.facebook.com&#x2F;",
          "instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
          "avatarLink": "https:&#x2F;&#x2F;dm6niys7elogn.cloudfront.net&#x2F;1628555664743_tumblr_ohxafbDzlJ1sdwnm3o1_1280.jpg",
          "about": "mình là kiều oanh sinh viên đại học bách khoa đà nẵng",
          "followed": false
        },
        "postId": 1,
        "commentDate": "2021-05-18 12:10",
        "content": "hey",
        "likeCount": 0,
        "childComments": null,
        "myComment": false,
        "liked": false
      }
    ],
    "myComment": false,
    "liked": false
  }
]
```

* **Error Response:**

    * **Code**: `404 NOT_FOUND` - Không tìm bài viết

## Thêm một comment

----
Thêm 1 comment vào bài viết

* **URL**: `/comment/post/{post_id}`

* **Method:** `POST`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name      | Type       | Description |
  |-----------| ---------- |:------:     | 
  | `content` | `string`   | Nội dung của comment (không được rỗng)   |
  | `parentId`| `int`      | ID của comment cha (nếu có)   |

* **Success Response:**

    * **Code:** `200 OK` - Kèm thông tin của Comment vừa được thêm
  
    * **Example**:
    
```json5
{
	"commentId": 1297,
	"parentId": 1293, // null nếu đây không phải là một rep comment
	"commenter": {
		"accountId": 1,
		"name": "Do Van Trinh (K18 DUT)",
		"email": "trinhvideo123@gmail.com",
		"postCount": 0,
		"followCount": 0,
		"bookmarkOnOwnPostCount": 0,
		"commentOnOwnPostCount": 0,
		"fbLink": "https:&#x2F;&#x2F;www.facebook.com&#x2F;",
		"instagramLink": "https:&#x2F;&#x2F;www.instagram.com&#x2F;",
		"avatarLink": "https://dm6niys7elogn.cloudfront.net/avatar2.png",
		"about": "Yêu màu hông, ghét sự giả dối. Thích đi du lịch, đi caffe các kiểu",
		"role": "ROLE_ADMIN",
		"followed": false
	},
	"postId": 1,
	"commentDate": "2021-12-25 21:02",
	"content": "&lt;p&gt;Hello my friend&lt;&#x2F;p&gt;",
	"likeCount": 0,
	"childComments": null,
	"liked": false,
	"myComment": true
}
```
    
* **Error Response:**

    * **Code**: `404 NOT_FOUND` - bài viết, comment cha, ... không tồn tại

    * **Code**: `403 FORBIDDEN` - chưa đăng nhập

## Sửa một comment

----
Sửa nội dung của một comment

* **URL**: `/comment/{comment_id}`

* **Method:** `PUT`
  
* **Header**: `Authorization: Bearer <token hiện tại>`

* **Request Body** `Content-Type: multipart/form-data`

  | Name        | Type       | Description                    |
  | ----------  |:------:    | ------------                   |
  | `content`   | `string`   | Nội dung mới (không được rỗng) |

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

    * **Code**: `404 NOT_FOUND` - Comment không tồn tại

    * **Code**: `403 FORBIDDEN / 401 UNAUTHORIZED` - chưa đăng nhập / không sở hữu

## Like / Unlike comment

----
Like / Unlike comment, nếu đã like thì sẽ trở thành unlike và ngược lại

* **URL**: `/comment/{comment_id}/like`

* **Method:** `PUT`

* **Header**: `Authorization: Bearer <token hiện tại>`

* **Success Response:**

    * **Code:** `200 OK` - Kèm theo số lượng like của comment vừa rồi
  
* **Error Response:**

    * **Code**: `404 NOT_FOUND` - Comment không tồn tại

    * **Code**: `403 FORBIDDEN` - Chưa đăng nhập 