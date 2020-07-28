<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>单文件上传</title>
    </head>
    <body>
        <form method="post" action="/upload" enctype="multipart/form-data" id = "form">
            <input type="file" name="file"><br>
            <input type="submit" value="提交">
        </form>
        <input type="file" name = "file" id = "file"><br>
        <input type="submit" value = "submit" onclick="sendFile()">
    </body>
    <script type="application/javascript">
        function sendFile() {
            var blob = document.getElementById("file").files[0];
            var xhr = new XMLHttpRequest();
            //false指同步上传，因为我的服务器内存较小，选择同步，如果追求速度，可以选择 //ture，异步上传
            xhr.open('POST', 'http://localhost:8082/upload', false);
            xhr.send(blob.slice(0, blob.size, blob.type))

        }
    </script>

    <script type="text/javascript">
        var BYTES_PER_CHUNK = 1024 * 1024; // 每个文件切片大小定为1MB .
        var slices;
        var totalSlices;

        //发送请求
        function sendRequest() {
            var blob = document.getElementById("file").files[0];
            var start = 0;
            var end;
            var index = 0;


            // 计算文件切片总数
            slices = Math.ceil(blob.size / BYTES_PER_CHUNK);
            totalSlices = slices;
            console.log('total slices is ' + totalSlices);
            while (start < blob.size) {
                end = start + BYTES_PER_CHUNK;
                if(end > blob.size) {
                    end = blob.size;
                }
                uploadFile(blob, index, start, end);
                start = end;
                index++;
                if ( index >= totalSlices )
                    window.location="error.htm";
            }
        }

        //上传文件
        function uploadFile(blob, index, start, end) {
            var fd;
            var chunk;
            var sliceIndex = blob.name;
            chunk = blob.slice(start,end);//切割文件
            fd = new FormData();
            fd.append(sliceIndex,chunk);
            var xhr = new XMLHttpRequest();
            //false指同步上传，因为我的服务器内存较小，选择同步，如果追求速度，可以选择 //ture，异步上传
            xhr.open('POST', 'http://localhost:8082/upload', false);
            xhr.send(fd);
//            xhr.send(blob)
            if ((xhr.status >= 200 && xhr.status < 300) || xhr.status == 304) {
                //等待2s
                setTimeout("", 2000);
            }
//            else {
//                uploadFile(blob, index, start, end);
//            }
        }
    </script>
</html>