package hello.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by richard on 3/14/19.
 */
public class Item {

    public static final Map<String, String> list = new HashMap<>(100);
    static {
        list.put("zhgz","test");
        list.put("zzjs", "test");
    }
}
