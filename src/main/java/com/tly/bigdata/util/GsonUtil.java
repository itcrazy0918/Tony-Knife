package com.tly.bigdata.util;

import com.google.gson.*;
import com.tly.bigdata.exception.CommonRuntimeException;

import java.lang.reflect.Type;

/**
 *
 * <pre>
 * GSON辅助工具。
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class GsonUtil {

	private static Gson gson;
	private static GsonBuilder gsonBuilder;
	private static Gson prettyPrintingGson;

	static {
		reload();
	}
	
	public static void reload(){
		reloadGson();
		reloadGsonBuilder();
		reloadPrettyPrintingGson();
	}	

	public static void reloadGson() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	}

	public static void reloadGsonBuilder() {
		gsonBuilder = new GsonBuilder();
	}

	public static void reloadPrettyPrintingGson() {
		prettyPrintingGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();
	}
	
	public static Gson getGson() {
        return gson;
    }

    public static GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }

    public static Gson getPrettyPrintingGson() {
        return prettyPrintingGson;
    }
    
    public static <T> T fromJson (String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    public static <T> T fromJson (String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
    
    public static <T> T fromJson (JsonElement json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    public static <T> T fromJson (JsonElement json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static String toJson (Object obj) {
        return gson.toJson(obj);
    }
	
	public static String toPrettyJson (Object obj) {
        return prettyPrintingGson.toJson(obj);
    }
	
	public static void main(String[] args) {
	    gsonBuilder.registerTypeAdapter(Coordinate.class, new CoordinateTypeAdapter());
	    Gson gson = gsonBuilder.create();
	    
	    String oldText = "[86.57402,5.14499,38.75355]";
	    System.out.println( oldText );
	    Coordinate oldC = gson.fromJson(oldText, Coordinate.class);
	    System.out.println( oldC );
	    
	    String newText = gson.toJson(oldC);
	    System.out.println( newText );
	    Coordinate newC = gson.fromJson(newText, Coordinate.class);
	    System.out.println( newC );
	}
}

class CoordinateTypeAdapter implements JsonSerializer<Coordinate>, JsonDeserializer<Coordinate> {

    @Override
    public Coordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonArray arr = json.getAsJsonArray();
            if (arr.size() != 3) {
                throw new CommonRuntimeException("CoordinateTypeAdapter.deserialize illegal json format[must has 3 float property]. text=" + json.toString());
            }
            
            Coordinate coordinate = new Coordinate();
            coordinate.setX( AoiUtil.toServerMeasure( arr.get(0).getAsFloat() ) );
            coordinate.setY( AoiUtil.toServerMeasure( arr.get(1).getAsFloat() ) );
            coordinate.setZ( AoiUtil.toServerMeasure( arr.get(2).getAsFloat() ) );
            return coordinate;
        }
        catch (Exception e) {
            throw new CommonRuntimeException("CoordinateTypeAdapter.deserialize illegal json format. text=" + json.toString() + ", Exception=" + e.getMessage());
        }
    }

    @Override
    public JsonElement serialize(Coordinate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray arr = new JsonArray();
        arr.add( AoiUtil.toJsonMeasure(src.getX()) );
        arr.add( AoiUtil.toJsonMeasure(src.getY()) );
        arr.add( AoiUtil.toJsonMeasure(src.getZ()) );
        return arr;
    }
    
}

class Coordinate {
    private int x;
    private int y;
    private int z;
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Coordinate [x=" + x + ", y=" + y + ", z=" + z + "]";
    }   
}


class AoiUtil {
    public final static float PROPORTION = 10000F;
    
    /**
     * 根据 json 配置的宽度/高度/深度，计算 服务端实际需要使用的宽度/高度/深度
     * @param jsonData
     * @return
     */
    public static int toServerMeasure (float jsonData) {
        float val = jsonData * PROPORTION;
        if (val > Integer.MAX_VALUE) {
            throw new CommonRuntimeException("illegal jsonData=" + jsonData);
        }
        return (int) val;
    }
    
    /**
     * 根据 服务端实际需要使用的宽度/高度/深度, 计算 json 配置的宽度/高度/深度
     * @param serverData
     * @return
     */
    public static float toJsonMeasure (int serverData) {
        return serverData / PROPORTION;
    }
}
