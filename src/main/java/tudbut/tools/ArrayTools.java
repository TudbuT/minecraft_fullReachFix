package tudbut.tools;

import de.tudbut.tools.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayTools {
    public static <T> T[] replace(T[] array, T repl, T with) { array=array.clone();for (int i=0;i<array.length;i++){if(array[i]!=null){if(array[i].equals(repl))array[i]=with;}else if(repl==null)array[i]=with;}return array; }

    @SafeVarargs
    public static <T> T[] arrayFromList(List<T> list, T... empty) {
        return list.toArray(empty);
    }

    public static <T, O> O[] getFromArray(T[] array, Getter<T, O> getter, O... ignore) throws Throwable {
        O[] os = (O[]) ArrayGetter.newArray(array.length, ignore.getClass().getComponentType());
        for (int i = 0; i < array.length; i++) {
            os[i] = getter.get(array[i]);
        }
        return os;
    }

    public interface Getter<T, O> {
        O get(T t) throws Throwable;
    }

    public static double[] convertToNative(Double[] doubles) {
        double[] d = new double[doubles.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = doubles[i];
        }
        return d;
    }

    public static int[] convertToNative(Integer[] ints) {
        int[] d = new int[ints.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = ints[i];
        }
        return d;
    }

    public static byte[] convertToNative(Byte[] bytes) {
        byte[] d = new byte[bytes.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = bytes[i];
        }
        return d;
    }

    public static <T> Map<String, String> mapFrom(T[] objects, Getter<T, String> getter) throws Throwable {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < objects.length; i++) {
            map.put(String.valueOf(i), getter.get(objects[i]));
        }

        map.put("len", String.valueOf(objects.length));

        return map;
    }

    public static <T> T[] fromMap(Map<String, String> map, Getter<String, T> getter, T... ignore) throws Throwable {
        int len = Integer.parseInt(map.get("len"));

        T[] t = ArrayGetter.newGenericArray(len, ignore);

        for (int i = 0; i < len; i++) {
            t[i] = getter.get(map.get(String.valueOf(i)));
        }

        return t;
    }
}
