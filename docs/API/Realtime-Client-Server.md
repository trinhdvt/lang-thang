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

1. Sau khi người dùng đăng nhập thành công, Client `subscribe` vào địa chỉ `/topic/notify` để lắng nghe

2. Nếu có thông tin trả về thì Client so sánh `accountId` trong message trả về với `accountId` hiện tại, nếu khớp nhau
   thì báo là có thông báo mới (ví dụ như thêm số `1` vô cái icon).
   **Lưu ý:** Message trả về chỉ chứa `accountId` để xác định xem đó là notify của ai, chứ không chứa dữ liệu. Sau khi
   họ bấm vào nút xem thông báo thì mới gửi request để load dữ liệu.

3. Sau khi đăng xuất thì `unsubscribe`

### Real-time comment

- Khá tương tự, chỉ khác là sẽ `subscribe` vào địa chỉ `/topic/post` ngay khi bắt đầu vào tranh chủ luôn

- Thông tin trả về bao gồm `postId`, `accountId`, `content`, ... Nên phải tracking được xem thử họ đang ở `postId` bao nhiêu,
  rồi so sánh nếu khớp thì hiển thị lên

### Code ví dụ

```html

<head>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"
            integrity="sha512-iKDtgDyTHjAitUDdLljGhenhPwrbBfqTKWO1mkhSFH3A7blITC9MhYon6SjnMhp4o0rADGw9yAC6EW4t5a4K3g=="
            crossorigin="anonymous"></script>
</head>

<body>
<script type="text/javascript">
    let websocket = null;
    let notify_subscription = null;

    // gọi hàm này lúc load page luôn
    function connect() {
        // Khởi tạo
        const socket = new SockJS("http://localhost:8080/socket-server");
        websocket = Stomp.over(socket);

        // Kết nối tới server
        websocket.connect({}, function (callback) {
            websocket.subscribe("/topic/post", function (message) {
                // có comment mới
                // render các kiểu ở đây
                console.log(JSON.parse(message.body));
            });
        }, function (error) {

        });
    }

    // khi nào đăng nhập xong thì gọi hàm này
    function notifySubscribe() {
        if (websocket != null) {
            notify_subscription = websocket.subscribe("/topic/notify", function (message) {
                //    có notify mới
                //    render các kiểu
            });
        }
    }

    // đăng xuất thì unsubscribe
    function notifyUnsubscribe() {
        if (notify_subscription != null) {
            notify_subscription.unsubscribe();
        }
    }
    
    // đóng kết nối khi họ tắt cửa sổ
    function disconnect() {
        if (websocket != null) {
            websocket.disconnect();
        }
    }
</script>
</body>
```