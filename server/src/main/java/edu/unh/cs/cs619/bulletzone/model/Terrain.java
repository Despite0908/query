package edu.unh.cs.cs619.bulletzone.model;

public enum Terrain {
    Normal, Rocky, Hilly, Forest, Water;

    public static Terrain fromByte(byte directionByte) {
        Terrain terrain = null;

        switch (directionByte) {
            case 0:
                terrain = Normal;
                break;
            case 2:
                terrain = Rocky;
                break;
            case 4:
                terrain = Hilly;
                break;
            case 6:
                terrain = Forest;
                break;
            case 8:
                terrain = Water;
            default:
                break;
        }

        return terrain;
    }

    public static byte toByte(Terrain terrain) {
        switch (terrain) {
            case Normal:
                return 0;
            case Rocky:
                return 2;
            case Hilly:
                return 4;
            case Forest:
                return 6;
            case Water:
                return 8;
            default:
                return -1;
        }
    }
}
