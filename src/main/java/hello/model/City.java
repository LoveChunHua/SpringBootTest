package hello.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richard on 3/14/19.
 */
public class City {

    public static final Map<String, String> list = new HashMap<>(100);
    static {
        list.put("t","test");
    }
}
