# jmx-exporter-agent
可热切换监控java进程的prometheus jmx exporter

官方的jmx-exporter使用的在java命令中加入agent参数的方式来启动一个agent，从而提供一个http server。这之中可能需要重启服务来启动监控。

而agent还有一种动态agent的方式，和arthas原理一致。通过agentmain方法，attach到java进程中。可以保证不用重启服务。

在http server开放的接口中增加一个 close 接口，用于动态关闭agent。

通过以上途径，实现可以随意开启随意关闭的jmx exporter
