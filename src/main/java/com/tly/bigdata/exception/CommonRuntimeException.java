package com.tly.bigdata.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <pre>
 * 自定义运行时异常
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class CommonRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -6922197809457025559L;

    private static final Logger logger = LoggerFactory.getLogger(CommonRuntimeException.class);

    public CommonRuntimeException(Throwable cause, String msg) {
        super(msg, cause);
    }

    public CommonRuntimeException(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);

        checkArgs(args);
    }

    public CommonRuntimeException(String msg) {
        super(msg);
    }

    public CommonRuntimeException(String format, Object... args) {
        super(String.format(format, args));

        checkArgs(args);
    }

    private void checkArgs (Object[] args) {
        if (args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof Throwable) {
                    logger.error("请注意改正: CommonRuntimeException 参数 和 JDK.Exception不一致: ", (Throwable) arg);
                }
            }
        }
    }

}
