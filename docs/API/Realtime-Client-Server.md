## Cách giao tiếp real-time giữa Client - Server

### Kĩ thuật sử dụng: WebSocket

* Client: [SockJS](https://github.com/sockjs/sockjs-client)
  , [STOMP](https://stomp-js.github.io/stomp-websocket/codo/extra/docs-src/Usage.md.html#toc_0)

```html

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"
        integrity="sha512-iKDtgDyTHjAitUDdLljGhenhPwrbBfqTKWO1mkhSFH3A7blITC9MhYon6SjnMhp4o0rADGw9yAC6EW4t5a4K3g=="
        crossorigin="anonymous"></script>
```

* Server: `Spring WebSocket`

### Real-time Notify

* Các bược thực hiện:

1. Sau khi người dùng đăng nhập thành công, Client `subscribe` vào địa chỉ `/topic/notification/{client_email}` để lắng nghe các thông báo mới nhất.

    * **Lưu ý:** Khi `Subscribe` phải có kèm theo `token`

2. Nếu có thông tin trả về thì có nghĩa là Client có thông báo mới

    * Thông tin trả về giống như 1 thông báo vậy, (xem lại [ở đây](Notification_API.md#Lấy-ra-danh-sách-các-thông-báo))

3. Sau khi đăng xuất thì `unsubscribe`

### Real-time comment

- Khá tương tự, chỉ khác là sẽ `subscribe` vào địa chỉ `/topic/post/{post_id}` ngay khi người dùng click vào 1 bài viết nào đó và khi `Subscribe` không cần kèm theo `token`

- Thông tin trả về giống như 1 comment ở bài viết vậy (xem lại [ở đây](Comment_API.md#Lấy-ra-danh-sách-các-comment-của-một-bài-viết))

### Code ví dụ

> Xem code mẫu [ở đây](../../src/main/resources/static/index.html)