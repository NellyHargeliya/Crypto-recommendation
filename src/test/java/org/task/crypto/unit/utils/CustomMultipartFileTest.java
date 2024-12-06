package org.task.crypto.unit.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.task.crypto.utils.CustomMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class CustomMultipartFileTest {

    @Test
    void testFileAttributes() throws IOException {
        byte[] content = "test content".getBytes();
        CustomMultipartFile file = new CustomMultipartFile("test.txt", "text/plain", content);

        assertEquals("test.txt", file.getName());
        assertEquals("test.txt", file.getOriginalFilename());
        assertEquals("text/plain", file.getContentType());
        assertFalse(file.isEmpty());
        assertEquals(content.length, file.getSize());
        assertArrayEquals(content, file.getBytes());
    }

    @Test
    void testInputStream() throws IOException {
        byte[] content = "stream content".getBytes();
        CustomMultipartFile file = new CustomMultipartFile("stream.txt", "text/plain", content);

        try (InputStream is = file.getInputStream()) {
            byte[] readContent = new byte[content.length];
            assertEquals(content.length, is.read(readContent));
            assertArrayEquals(content, readContent);
        }
    }

    @Test
    void testTransferTo(@TempDir File tempDir) throws IOException {
        byte[] content = "content to transfer".getBytes();
        CustomMultipartFile file = new CustomMultipartFile("transfer.txt", "text/plain", content);
        File dest = new File(tempDir, "destination.txt");

        file.transferTo(dest);
        assertTrue(dest.exists());
        assertArrayEquals(content, Files.readAllBytes(dest.toPath()));
    }

    @Test
    void testEmptyFile() throws IOException {
        byte[] content = new byte[0];
        CustomMultipartFile file = new CustomMultipartFile("empty.txt", "text/plain", content);

        assertTrue(file.isEmpty());
        assertEquals(0, file.getSize());
    }

}