package edu.unh.cs.cs619.bulletzone.model.improvements;

public enum ImprovementMapper {

    Road, Wall, Deck;

    public static ImprovementMapper fromByte(byte directionByte) {
        ImprovementMapper improvement = null;

        switch (directionByte) {
            case 0:
                improvement = Road;
                break;
            case 1:
                improvement = Wall;
                break;
            case 2:
                improvement = Deck;
                break;
            default:
                break;
        }

        return improvement;
    }

    public static byte toByte(ImprovementMapper improvement) {
        switch (improvement) {
            case Road:
                return 0;
            case Wall:
                return 1;
            case Deck:
                return 2;
            default:
                return -1;
        }
    }
}
