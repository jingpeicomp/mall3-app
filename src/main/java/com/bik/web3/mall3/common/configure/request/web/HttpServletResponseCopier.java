package com.bik.web3.mall3.common.configure.request.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author Mingo.Liu
 */
public class HttpServletResponseCopier extends HttpServletResponseWrapper {
    private ServletOutputStream outputStream;

    private PrintWriter writer;

    private ServletOutputStreamCopier copier;

    public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (null != writer) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (null == outputStream) {
            outputStream = getResponse().getOutputStream();
            copier = new ServletOutputStreamCopier(outputStream);
        }

        return copier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (null != outputStream) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (null == writer) {
            copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (null != writer) {
            writer.flush();
        } else if (null != outputStream) {
            copier.flush();
        }
    }

    public byte[] getCopy() {
        if (null != copier) {
            return copier.getCopy();
        } else {
            return new byte[0];
        }
    }
}