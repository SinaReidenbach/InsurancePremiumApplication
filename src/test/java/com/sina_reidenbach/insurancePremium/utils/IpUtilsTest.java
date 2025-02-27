package com.sina_reidenbach.insurancePremium.utils;

import org.junit.jupiter.api.Test;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class IpUtilsTest {

    @Mock
    public IpUtils ipUtils;
    @Test
    public void testGetClientIp_WithXForwardedForHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.100");

        String clientIp = IpUtils.getClientIp(request);

        assertEquals("192.168.1.100", clientIp, "Sollte die IP aus dem X-Forwarded-For Header zurückgeben");
    }

    @Test
    public void testGetClientIp_WithMultipleXForwardedForIps() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.100, 10.0.0.1, 127.0.0.1");

        String clientIp = IpUtils.getClientIp(request);

        assertEquals("192.168.1.100", clientIp, "Sollte die erste IP aus der Liste zurückgeben");
    }

    @Test
    public void testGetClientIp_WithoutXForwardedForHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("10.0.0.2");

        String clientIp = IpUtils.getClientIp(request);

        assertEquals("10.0.0.2", clientIp, "Sollte die RemoteAddr zurückgeben, wenn kein Header vorhanden ist");
    }

    @Test
    public void testGetClientIp_WithEmptyXForwardedForHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("10.0.0.3");

        String clientIp = IpUtils.getClientIp(request);

        assertEquals("10.0.0.3", clientIp, "Sollte die RemoteAddr zurückgeben, wenn der Header leer ist");
    }
}
