package com.healsweets.config;

import com.healsweets.entity.Member;
import com.healsweets.entity.Product;
import com.healsweets.repository.MemberRepository;
import com.healsweets.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeProducts();
        initializeTestMember();
    }

    private void initializeProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = Arrays.asList(
            Product.builder()
                .name("ミックスナッツ スイーツボックス")
                .description("厳選されたナッツを使用したアレルゲンフリーのスイーツボックス。自然な甘さとナッツの香ばしさが楽しめます。")
                .price(BigDecimal.valueOf(540))
                .category("allergen-free")
                .imageUrl("/images/item01.jpg")
                .quantity("8個入り")
                .expiryDays("製造日より30日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(List.of("ナッツ類"))
                .features(Arrays.asList("✓ グルテンフリー", "✓ 卵不使用", "✓ 乳製品不使用", "✓ 添加物不使用"))
                .build(),

            Product.builder()
                .name("グルテンフリー クッキーボックス")
                .description("米粉を使用したグルテンフリーのクッキー。サクサクの食感とやさしい甘さが特徴です。")
                .price(BigDecimal.valueOf(780))
                .category("allergen-free")
                .imageUrl("/images/item02.jpg")
                .quantity("12枚入り")
                .expiryDays("製造日より45日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(List.of())
                .features(Arrays.asList("✓ グルテンフリー", "✓ 卵不使用", "✓ 国産米粉100%", "✓ 添加物不使用"))
                .build(),

            Product.builder()
                .name("アレルゲンフリー スナックパック")
                .description("特定原材料28品目不使用のスナックパック。お子様にも安心してお召し上がりいただけます。")
                .price(BigDecimal.valueOf(690))
                .category("allergen-free")
                .imageUrl("/images/item03.jpg")
                .quantity("6袋入り")
                .expiryDays("製造日より60日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(List.of())
                .features(Arrays.asList("✓ 28品目不使用", "✓ 添加物不使用", "✓ 国産素材使用", "✓ 個包装"))
                .build(),

            Product.builder()
                .name("低糖質 グルメクッキー")
                .description("糖質を抑えながらも、深いコクと香ばしさを実現した本格派クッキーです。厳選されたアーモンドプードルとバターを使用し、一枚一枚丁寧に焼き上げました。")
                .price(BigDecimal.valueOf(620))
                .category("low-sugar")
                .imageUrl("/images/item04.jpg")
                .quantity("12枚入り（約200g）")
                .expiryDays("製造日より60日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(Arrays.asList("小麦", "卵", "乳製品", "アーモンド"))
                .features(Arrays.asList("✓ 糖質70%カット（当社比）", "✓ 添加物不使用", "✓ 国産素材使用", "✓ 個包装で保存しやすい"))
                .build(),

            Product.builder()
                .name("低糖質 マカロンギフト")
                .description("糖質を大幅カットしながらも、サクッとした外側としっとりした中身の絶妙な食感を実現。")
                .price(BigDecimal.valueOf(750))
                .category("low-sugar")
                .imageUrl("/images/item05.jpg")
                .quantity("8個入り")
                .expiryDays("製造日より14日")
                .storageMethod("要冷蔵（10℃以下）")
                .allergens(Arrays.asList("卵", "乳製品", "アーモンド"))
                .features(Arrays.asList("✓ 糖質60%カット", "✓ 天然素材使用", "✓ 着色料不使用", "✓ ギフトボックス付き"))
                .build(),

            Product.builder()
                .name("低糖質 フレッシュフルーツタルト")
                .description("新鮮なフルーツをたっぷり使用した低糖質タルト。甘さ控えめでフルーツの自然な甘みを楽しめます。")
                .price(BigDecimal.valueOf(710))
                .category("low-sugar")
                .imageUrl("/images/item06.jpg")
                .quantity("1ホール（4号サイズ）")
                .expiryDays("製造日より3日")
                .storageMethod("要冷蔵（10℃以下）")
                .allergens(Arrays.asList("小麦", "卵", "乳製品"))
                .features(Arrays.asList("✓ 糖質50%カット", "✓ 季節のフルーツ使用", "✓ 無添加クリーム", "✓ 低GI"))
                .build(),

            Product.builder()
                .name("カカオ70% リッチチョコレートケーキ")
                .description("カカオ70%の上質なチョコレートを贅沢に使用。濃厚でありながら後味すっきりの大人の味わい。")
                .price(BigDecimal.valueOf(560))
                .category("cacao")
                .imageUrl("/images/item07.jpg")
                .quantity("1ホール（5号サイズ）")
                .expiryDays("製造日より5日")
                .storageMethod("要冷蔵（10℃以下）")
                .allergens(Arrays.asList("小麦", "卵", "乳製品"))
                .features(Arrays.asList("✓ カカオ70%使用", "✓ ベルギー産チョコレート", "✓ ポリフェノール豊富", "✓ 甘さ控えめ"))
                .build(),

            Product.builder()
                .name("カカオ70% プレミアムブラウニー")
                .description("しっとり濃厚なブラウニー。カカオ70%のビターな味わいとナッツの食感が絶妙にマッチ。")
                .price(BigDecimal.valueOf(990))
                .category("cacao")
                .imageUrl("/images/item08.jpg")
                .quantity("6個入り")
                .expiryDays("製造日より10日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(Arrays.asList("小麦", "卵", "乳製品", "くるみ"))
                .features(Arrays.asList("✓ カカオ70%使用", "✓ くるみたっぷり", "✓ 個包装", "✓ ギフト対応"))
                .build(),

            Product.builder()
                .name("カカオ70% トリュフコレクション")
                .description("口の中でとろけるなめらかな食感。カカオ70%の深い味わいを堪能できる贅沢なトリュフ。")
                .price(BigDecimal.valueOf(770))
                .category("cacao")
                .imageUrl("/images/item09.jpg")
                .quantity("9個入り")
                .expiryDays("製造日より21日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(Arrays.asList("乳製品"))
                .features(Arrays.asList("✓ カカオ70%使用", "✓ 3種のフレーバー", "✓ ギフトボックス付き", "✓ 手作り"))
                .build(),

            Product.builder()
                .name("カカオ70% 職人のボンボンショコラ")
                .description("一つ一つ職人が手作りした本格ボンボンショコラ。様々なフレーバーをお楽しみいただけます。")
                .price(BigDecimal.valueOf(620))
                .category("cacao")
                .imageUrl("/images/item10.jpg")
                .quantity("12個入り")
                .expiryDays("製造日より30日")
                .storageMethod("直射日光・高温多湿を避けて保存")
                .allergens(Arrays.asList("乳製品", "アーモンド"))
                .features(Arrays.asList("✓ カカオ70%使用", "✓ 職人手作り", "✓ 6種のフレーバー", "✓ ギフトボックス付き"))
                .build()
        );

        productRepository.saveAll(products);
        log.info("商品データを{}件登録しました", products.size());
    }

    private void initializeTestMember() {
        if (memberRepository.existsByEmail("tanaka@example.com")) {
            return;
        }

        Member testMember = Member.builder()
                .lastName("田中")
                .firstName("太郎")
                .email("tanaka@example.com")
                .password(passwordEncoder.encode("password123"))
                .phone("090-1234-5678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .postalCode("123-4567")
                .prefecture("東京都")
                .city("渋谷区")
                .address1("○○ 1-2-3")
                .address2("○○マンション 101号室")
                .allergies(Arrays.asList("小麦", "乳製品"))
                .newsletter(true)
                .build();

        memberRepository.save(testMember);
        log.info("テスト会員を登録しました: {}", testMember.getEmail());
    }
}
