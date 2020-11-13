package com.java;

import com.java.dto.CodeValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @date 2020/10/30
 */
@Slf4j
public class Test {

    public static void main(String[] args) {
        /*String reason = null;
        if (isInReasons(reason, initCv())) {
            log.info(String.valueOf(true));
        } else {
            log.error(String.valueOf(false));
        }*/

        dateTest();
    }

    /**
     * 初始化 codeValues
     *
     * @return codeValues
     */
    private static List<CodeValue> initCv() {
        List<CodeValue> cvs = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            CodeValue cv = new CodeValue();
            cv.setValue("A" + (i + 1));
            cvs.add(cv);
        }
        CodeValue cv = new CodeValue();
        cv.setValue("B1");
        cvs.add(cv);
        return cvs;
    }

    /**
     * 差异原因字段匹配值集
     *
     * @param reason  差异原因字段（取前两个字符）
     * @param reasons 差异原因值集
     * @return        是否匹配
     */
    @Deprecated
    private static boolean isInReasons(String reason, List<CodeValue> reasons) {
        int len = 2;
        if (StringUtils.isBlank(reason) || reason.trim().length() < len) {
            return false;
        }
        if (reasons == null || reasons.isEmpty()) {
            return false;
        }
        // 取前两个字符
        String reasonSplit = reason.trim().substring(0, len);
        for (CodeValue cv : reasons) {
            if (reasonSplit.equalsIgnoreCase(cv.getValue())) {
                return true;
            }
        }
        return false;
    }


    private static void dateTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());
        sdf = new SimpleDateFormat("hhmmss");
        String todayTime = sdf.format(new Date());
        log.info(today);
        log.info(todayTime);
    }
}
