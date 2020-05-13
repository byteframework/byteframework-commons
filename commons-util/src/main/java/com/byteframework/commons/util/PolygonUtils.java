package com.byteframework.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PolygonUtils {
    /**
     * 判断一个点是否在指定的多边形内
     *
     * @param px        点x坐标
     * @param py        点y坐标
     * @param polygonXA 多边形X点坐标序列
     * @param polygonYA 多边形y点坐标序列
     * @return
     */
    public static boolean isPointInPolygon(double px, double py, List<Double> polygonXA, List<Double> polygonYA) {
        boolean isInside = false;
        double ESP = 1e-9;
        int count = 0;
        double linePoint1x;
        double linePoint1y;
        double linePoint2x = 180;
        double linePoint2y;

        linePoint1x = px;
        linePoint1y = py;
        linePoint2y = py;

        for (int i = 0; i < polygonXA.size() - 1; i++) {
            double cx1 = polygonXA.get(i);
            double cy1 = polygonYA.get(i);
            double cx2 = polygonXA.get(i + 1);
            double cy2 = polygonYA.get(i + 1);
            if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) {
                return true;
            }
            if (Math.abs(cy2 - cy1) < ESP) {
                continue;
            }

            if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                if (cy1 > cy2)
                    count++;
            } else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                if (cy2 > cy1)
                    count++;
            } else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                count++;
            }
        }
        if (count % 2 == 1) {
            isInside = true;
        }

        return isInside;
    }

    private static double Multiply(double px0, double py0, double px1, double py1, double px2, double py2) {
        return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
    }

    private static boolean isPointOnLine(double px0, double py0, double px1, double py1, double px2, double py2) {
        boolean flag = false;
        double ESP = 1e-9;
        if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0)
                && ((py0 - py1) * (py0 - py2) <= 0)) {
            flag = true;
        }
        return flag;
    }

    private static boolean isIntersect(double px1, double py1, double px2, double py2, double px3, double py3, double px4,
                                       double py4) {
        boolean flag = false;
        double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
        if (d != 0) {
            double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;
            double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;
            if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
                flag = true;
            }
        }
        return flag;
    }


    public static double[] convertDistanceToLonLat(float distance, double[] lonlat, float angle) {
        if (distance == 0) return lonlat;
        // 将距离转换成经度的计算公式
        double lon = lonlat[0] + (distance / 1000 * Math.sin(angle * Math.PI / 180)) / (111 * Math.cos(lonlat[1] * Math.PI / 180));
        // 将距离转换成纬度的计算公式
        double lat = lonlat[1] + (distance / 1000 * Math.cos(angle * Math.PI / 180)) / 111;
        return new double[]{lon, lat};
    }

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }


    public static void main(String[] args) {
        //区块电子围栏经度集合
        List<Double> pointxs;
        //区块电子围栏纬度集合
        List<Double> pointys;
        //区块东北角经纬度 mmx[0]: lon, mmx[1]: lat
        double[] mmx = {Double.MIN_VALUE, Double.MIN_VALUE};
        //区块西南角经纬度 mmn[0]: lon, mmn[1]: lat
        double[] mmn = {Double.MAX_VALUE, Double.MAX_VALUE};

        String points = "114.128835,22.556151	114.122906,22.562492;114.130667,22.562425;114.13153,22.562425;114.133686,22.560556;114.137351,22.556484;114.137638,22.551545;114.119816,22.548474;114.120103,22.549942;114.120175,22.550877;114.120966,22.552079;114.121541,22.552546;114.1219,22.553547;114.121325,22.555817;114.121253,22.557552;114.121397,22.559288;114.121684,22.560489;114.12269,22.561824";
        String[] ps = points.split(";");
        //少于3个点返回
        if (ps.length < 3) return;
        List<Double> xs = new ArrayList<Double>();
        List<Double> ys = new ArrayList<Double>();
        for (String p : ps) {
            if (StringUtils.isBlank(p)) continue;
            String[] ll = p.split(",");
            if (ll.length != 2 || StringUtils.isBlank(ll[0]) || StringUtils.isBlank(ll[1])) continue;
            double lon = Double.valueOf(ll[0]);
            double lat = Double.valueOf(ll[1]);
            xs.add(lon);
            ys.add(lat);
            if (mmx[0] < lon) mmx[0] = lon;
            if (mmn[0] > lon) mmn[0] = lon;
            if (mmx[1] < lat) mmx[1] = lat;
            if (mmn[1] > lat) mmn[1] = lat;
        }
        pointxs = xs;
        pointys = ys;

        System.out.println(mmx[0] + "," + mmx[1] + "; " + mmn[0] + "," + mmn[1]);

        double lon = 114.122759;
        double lat = 22.550609;
        if (lon == 0.0 && lat == 0.0) return;
        //如果点不在多边形所示的矩形范围(东北角、西南角所示的矩形)内，则不需要后续复杂计算，直接返回false,
        if (lon < mmn[0] || lon > mmx[0] || lat < mmn[1] || lat > mmx[1]) return;
        System.out.println(PolygonUtils.isPointInPolygon(lon, lat, pointxs, pointys));
    }

}
