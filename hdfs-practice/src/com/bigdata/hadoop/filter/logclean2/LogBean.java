package com.bigdata.hadoop.filter.logclean2;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LogBean implements Writable {

    private String remoteAddr; // 客户端地址
    private String remoteUser; // 客户端名称，忽略时使用 -
    private String timeLocal; // 访问时间和时区
    private String request; //请求 url 和 http 协议
    private String status; // 请求状态, 200成功
    private String body; // 发送给客户端主体内容
    private String httpRefer; // 记录从哪个页面链接过来的
    private String userAgent; // 客户端浏览器代理信息

    private boolean valid = true; // 记录数据是否合法

    public LogBean() {}

    public LogBean(String remoteAddr, String remoteUser, String timeLocal, String request, String status, String body, String httpRefer, String userAgent) {
        this.remoteAddr = remoteAddr;
        this.remoteUser = remoteUser;
        this.timeLocal = timeLocal;
        this.request = request;
        this.status = status;
        this.body = body;
        this.httpRefer = httpRefer;
        this.userAgent = userAgent;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public void setTimeLocal(String timeLocal) {
        this.timeLocal = timeLocal;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setHttpRefer(String httpRefer) {
        this.httpRefer = httpRefer;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.valid);
        // 分隔符 \001
        sb.append("\001").append(this.remoteAddr);
        sb.append("\001").append(this.remoteUser);
        sb.append("\001").append(this.timeLocal);
        sb.append("\001").append(this.request);
        sb.append("\001").append(this.status);
        sb.append("\001").append(this.body);
        sb.append("\001").append(this.httpRefer);
        sb.append("\001").append(this.userAgent);
        return sb.toString();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(remoteAddr);
        out.writeUTF(remoteUser);
        out.writeUTF(timeLocal);
        out.writeUTF(request);
        out.writeUTF(status);
        out.writeUTF(body);
        out.writeUTF(httpRefer);
        out.writeUTF(userAgent);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        remoteAddr = in.readUTF();
        remoteUser = in.readUTF();
        timeLocal = in.readUTF();
        request = in.readUTF();
        status = in.readUTF();
        body = in.readUTF();
        httpRefer = in.readUTF();
        userAgent = in.readUTF();
    }
}
