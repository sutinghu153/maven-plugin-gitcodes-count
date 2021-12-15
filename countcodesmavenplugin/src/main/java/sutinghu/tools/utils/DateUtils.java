package sutinghu.tools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @data2021/12/15,14:07
 * @authorsutinghu
 */
public class DateUtils {

    public static Date getDate(String t){
        return new Date(t);
    }

    public static Date getDateTime(String times){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date time = null;

        try {
            time = simpleDateFormat.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

}
