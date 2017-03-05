# protocol
try to describe protocols of apllication layer into programming language

按照时间顺序来吧：

1.socket工程——暂不涉及什么协议，主要是一些socket通信的网络编程

1.1.com.nice.protocol.socket.nio中的代码是对java nio编程的简单实现：
包括把服务器与客户端的连接读写操作封装到了一个类包括把服务器与客户端的连接读写操作封装到了一个类中
客户端的读写线程，服务端的读写线程
其中客户端的读写线程被抽象成了一个，是读线程还是写线程依赖于其中的成员变量operationtype，该变量为一枚举变量，取值范围包括”读“和”写“，该枚举定义在com.nice.protocol.socket.util中

1.2.com.nice.protocol.socket.main中的代码是通过带入口函数的类实现以下nio客户端和服务器的功能：
包括一个发送客户端，一个接收客户端，一个服务端接收中心，以及一个服务端转发中心（模拟一个多人聊天室的功能）
