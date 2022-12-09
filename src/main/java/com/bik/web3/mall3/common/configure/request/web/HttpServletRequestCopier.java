package com.bik.web3.mall3.common.configure.request.web;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * HttpServletRequestCopier
 *
 * @author Mingo.Liu
 */
public class HttpServletRequestCopier extends HttpServletRequestWrapper {

    /**
     * output stream
     */
    private ServletInputStream inputStream;

    /**
     * reader
     */
    private BufferedReader reader;

    /**
     * copy input stream
     */
    private ServletInputStreamCopier copier;


    public HttpServletRequestCopier(HttpServletRequest request) {
        super(request);

    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (null != this.reader) {
            throw new IllegalStateException("getReader() has already been called on this request....");
        }
        if (null == this.inputStream) {
            this.inputStream = getRequest().getInputStream();
            this.copier = new ServletInputStreamCopier(this.inputStream);
        }
        return this.copier;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (null != inputStream) {
            throw new IllegalStateException("getInputStream() has already been called on this request.");
        }

        if (null == reader) {
            copier = new ServletInputStreamCopier(getRequest().getInputStream());
            reader = new BufferedReader(new InputStreamReader(copier));
        }

        return reader;
    }

    /**
     * get copy
     *
     * @return copy
     */
    public byte[] getCopy() {
        if (null != this.copier) {
            return this.copier.getCopy();
        }
        return new byte[0];
    }
}
