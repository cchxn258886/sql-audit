package com.example.sqlexamine.utils;

import com.example.sqlexamine.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;

/**
 * @Author chenl
 * @Date 2022/4/20 5:06 下午
 */
@Slf4j
public class RdsTimeUtils {
    /*"2022-04-20Z" 2018-06-12T15:00Z yyyy-mm-dd*/

    public static String createRdsNeedTimeNoHourMinSecond(Date date){
        String format = DateFormatUtils.format(date, "yyyy-MM-dd");
        return format+"Z";
    }
    public static String createRdsNeedTimeWithHourMinNoSecond(Date date){
        String format = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm");
        String replace = format.replace(" ", "T");
        return replace+"Z";
    }
    /**
     * 传入2020-10-01 12:00:00
     * @return 2020-10-01Z
     * */
    public static String createRdsNeedTimeSplit(String dateString) throws BizException{
        try{
            Date date = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd HH:mm:ss"});
        }catch (ParseException e){
            log.error("解析失败:{}",e.getMessage());
            throw new BizException("解析失败");
        }
        String[] s = dateString.split(" ");
        return s[0]+"Z";
    }
    public static Date  getTodayZeroHour(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00");
        try {
            return DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd HH:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date getTodayAllHour(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59");
        try {
            Date resultDate = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd HH:mm:ss"});
            return resultDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeDingRobotSign(Long timestamp,String accessToken) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String stringToSign = timestamp + "\n" + accessToken;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(accessToken.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }
}
