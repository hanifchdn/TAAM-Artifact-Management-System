package com.example.b07demosummer2024;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddItemFragmentTest {

    @Test
    public void testConstructor_setsFieldsCorrectly() {
        Artifact artifact = new Artifact("LOT123", "Vase", "A ceramic vase", "Ceramics", "Ceramic", "Tang Dynasty");
        assertEquals("LOT123", artifact.getLOT());
        assertEquals("Vase", artifact.getName());
        assertEquals("A ceramic vase", artifact.getDescription());
        assertEquals("Ceramics", artifact.getCategory());
        assertEquals("Ceramic", artifact.getMaterial());
        assertEquals("Tang Dynasty", artifact.getDynasty());
    }

    @Test
    public void testSetHeight_getHeightReturnsCorrectValue() {
        Artifact artifact = new Artifact();
        artifact.setHeight("10");
        assertEquals("10", artifact.getHeight());
    }

    @Test
    public void testSetWidth_getWidthReturnsCorrectValue() {
        Artifact artifact = new Artifact();
        artifact.setWidth("5");
        assertEquals("5", artifact.getWidth());
    }

    @Test
    public void testSetImageUrl_getImageUrlReturnsCorrectValue() {
        Artifact artifact = new Artifact();
        artifact.setImageUrl("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", artifact.getImageUrl());
    }
}