package com.SE11.ReceiptOCR.shared;

public class ExtractedField {
    private String inferText;
    private boolean lineBreak;
    private BoundingPoly boundingPoly;

    // 생성자
    public ExtractedField(String inferText, boolean lineBreak, BoundingPoly boundingPoly) {
        this.inferText = inferText;
        this.lineBreak = lineBreak;
        this.boundingPoly = boundingPoly;
    }

    // Getter 및 Setter 메서드
    public String getInferText() {
        return inferText;
    }

    public void setInferText(String inferText) {
        this.inferText = inferText;
    }

    public boolean lineBreak() {
        return lineBreak;
    }

    public void setlineBreak(boolean lineBreak) {
        this.lineBreak = lineBreak;
    }

    public BoundingPoly getBoundingPoly() {
        return boundingPoly;
    }

    public void setBoundingPoly(BoundingPoly boundingPoly) {
        this.boundingPoly = boundingPoly;
    }

    @Override
    public String toString() {
        return "ExtractedField{" +
                "inferText='" + inferText + '\'' +
                ", lineBreak=" + lineBreak +
                ", boundingPoly=" + boundingPoly +
                '}';
    }
}

