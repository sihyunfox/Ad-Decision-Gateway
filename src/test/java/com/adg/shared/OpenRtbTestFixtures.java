package com.adg.shared;

import com.adg.shared.dto.openrtb.*;

import java.util.List;

/**
 * OpenRTB BidRequest 테스트용 픽스처.
 * <p>광고 응답이 가능한 요청: id, imp(banner w/h), site, device(geo), user 등 파이프라인/다운스트림에서 사용하는 항목 포함.</p>
 */
public final class OpenRtbTestFixtures {

    private OpenRtbTestFixtures() {
    }

    /**
     * 광고 응답이 가능한 전체 OpenRTB 2.x BidRequest.
     * <p>포함: id, imp[0].id + imp[0].banner.{w,h}, site.id + publisher, device.{os,geo}, user.id</p>
     */
    public static BidRequest fullBidRequestForAdResponse() {
        return BidRequest.builder()
                .id("full-req-1")
                .imp(List.of(
                        Imp.builder()
                                .id("placement-1")
                                .banner(Banner.builder().w(300).h(250).build())
                                .build()))
                .site(Site.builder()
                        .id("site-1")
                        .domain("test.example.com")
                        .publisher(Publisher.builder().id("pub-1").name("Test Pub").build())
                        .build())
                .app(App.builder()
                        .id("app-1")
                        .bundle("com.example.app")
                        .publisher(Publisher.builder().id("pub-1").build())
                        .build())
                .device(Device.builder()
                        .os("Android")
                        .ua("Mozilla/5.0 (Linux; Android 10) ...")
                        .ifa("device-idfa-1")
                        .geo(Geo.builder().country("KOR").region("11").city("Seoul").build())
                        .build())
                .user(User.builder().id("user-1").build())
                .at(2)
                .tmax(500)
                .cur(List.of("USD"))
                .build();
    }
}
