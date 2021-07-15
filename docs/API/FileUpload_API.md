# `Upload` API Document

## Upload 1 file ảnh lên server

----
Trả về một đường dẫn public có thể truy cập được

* **URL**: `/upload`

* **Method:** `POST`

* **Headers:** `Authorization: Bearer <token hiện tại>`

* **Request Body:** `Content-Type: multipart/form-data`

  | Name     | Type       | Description  | 
  | -------- |:------:    | ------------ | 
  | `image`  | `multipart`| File ảnh     | 
  
* **Success Response:**

    * **Code:** `200 OK` - Kèm theo đường dẫn public có thể truy cập được
    
    * **Example:**
  
```json5
{
  "url": "abc.xyz.jpg"
}
```
  
* **Error Response:**

    * **Code**: `403 FORBIDDEN` - Chưa đăng nhập
  
    * **Code**: `400 BAD_REQUEST` - File upload không phải là file ảnh hoặc có kích thước lớn hơn `2MB`
