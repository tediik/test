package com.jm.online_store.config;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Description;
import com.jm.online_store.model.News;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * класс первичного заполнения таблиц.
 * <p>
 * для первичного заполнения базы данных раскомментировать аннотацию
 * "@PostConstruct" и поменять значение  ключа "spring.jpa.hibernate.ddl-auto"
 * в файле "application.yml" с "update" на "create" или "create-drop".
 */
@AllArgsConstructor
@Component
@Data
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final CategoriesService categoriesService;
    private final ProductService productService;
    private final NewsService newsService;
    private final OrderService orderService;
    private final ProductInOrderService productInOrderService;
    private final BasketService basketService;
    private final StockService stockService;
    private final SharedStockService sharedStockService;
    private final SentStockService sentStockService;

    /**
     * Основной метод для заполнения базы данных.
     * Вызов методов добавлять в этод метод.
     * Следить за последовательностью вызова.
     */
    //@PostConstruct
    public void initDataBaseFilling() {
        roleInit();
        newsInit();
        productInit();
        ordersInit();
        stockInit();
        sharedStockInit();
        sentStockInit();
        paginationNewsAndStocksInit();
    }

    /**
     * Метод конфигурирования и первичного заполнения таблиц:
     * ролей, юзеров и корзины.
     */
    private void roleInit() {
        Role adminRole = new Role("ROLE_ADMIN");
        Role customerRole = new Role("ROLE_CUSTOMER");
        Role managerRole = new Role("ROLE_MANAGER");

        roleService.addRole(adminRole);
        roleService.addRole(customerRole);
        roleService.addRole(managerRole);

        User admin = new User("admin@mail.ru", "1");
        User manager = new User("manager@mail.ru", "1");
        User customer = new User("customer@mail.ru", "1");

        Optional<Role> admnRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Optional<Role> managRole = roleService.findByName("ROLE_MANAGER");

        Set<Role> customerRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();

        customerRoles.add(custRole.get());
        adminRoles.add(admnRole.get());
        adminRoles.add(custRole.get());
        managerRoles.add(managRole.get());

        manager.setRoles(managerRoles);
        admin.setRoles(adminRoles);
        customer.setRoles(customerRoles);

        userService.addUser(manager);
        userService.addUser(customer);
        userService.addUser(admin);

        Product product_1 = new Product("apple", 100000D, 10, 0.1);
        Product product_2 = new Product("samsung", 80000D, 100, 0.9);
        Product product_3 = new Product("xiaomi", 30000D, 50, 0.5);

        productService.saveProduct(product_1);
        productService.saveProduct(product_2);
        productService.saveProduct(product_3);

        Set<Product> productSet = new HashSet<>();
        productSet.add(product_1);
        productSet.add(product_2);
        productSet.add(product_3);

        customer = userService.findByEmail("customer@mail.ru").get();
        customer.setFavouritesGoods(productSet);
        userService.updateUser(customer);

        SubBasket subBasket_1 = new SubBasket();
        subBasket_1.setProduct(product_1);
        subBasket_1.setCount(1);
        basketService.addBasket(subBasket_1);
        SubBasket subBasket_2 = new SubBasket();
        subBasket_2.setProduct(product_3);
        subBasket_2.setCount(1);
        basketService.addBasket(subBasket_2);
        List<SubBasket> subBasketList = new ArrayList<>();
        subBasketList.add(subBasket_1);
        subBasketList.add(subBasket_2);
        customer.setUserBasket(subBasketList);
        userService.updateUser(customer);

        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            userService.addUser(new User("customer" + i + "@mail.ru",
                    User.DayOfWeekForStockSend.values()[random.nextInt(6)],
                    String.valueOf(i)));
        }
    }

    /**
     * Метод первичного тестового заполнения новостей.
     */
    private void newsInit() {
        News firstNews = News.builder()
                .title("Акция от XP-Pen: Выигай обучение в Skillbox!")
                .anons("Не пропустите розыгрыш потрясающих призов.")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">Если вы любите создавать и повсюду ищите вдохновение, то следующая" +
                        " новость для вас!</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left:" +
                        " 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial," +
                        " sans-serif;\">XP-Pen проводят акицию с невроятно крутым призовым фоном, вы можете выиграть" +
                        " один из сертификатов на годовое обучение 2D или 3D рисованию в Skillbox, а также фирменные" +
                        " сувениры от бренда.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px;" +
                        " padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial," +
                        " sans-serif;\">Что нужно делать?</p><ul style=\"margin-right: 0px; margin-bottom: 0px;" +
                        " margin-left: 0px; padding: 0px; list-style-type: none; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\"><li style=\"margin: 0px; padding: 0px;\">1.Купить" +
                        " в <b>Online-Shop</b> любой графический планшет или интерактивный дисплей XP-Pen с 15" +
                        " августа по 15 сентября 2020 года.</li><li style=\"margin: 0px; padding: 0px;\">2.Пришлите" +
                        " серийный номер изделия на эл. почту sales_ru@xp-pen.com</li>X – XP-Pen подведут итоги" +
                        " методом рандома, так что шанс есть у каждого!</li></ul><p style=\"margin-right: 0px;" +
                        " margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\">Вы только взгляните на эти призы!</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">1 сертификат на" +
                        " обучение в школе SkillBox по курсу «Профессия 2D-художник»</p><p style=\"margin-right:" +
                        " 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на обучение в школе SkillBox по" +
                        " курсу «Профессия 3D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em;" +
                        " margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;," +
                        " Arial, sans-serif;\">5 наборов фирменных сувениров от XP-Pen (в набор входит рюкзачок" +
                        " XP-Pen, брелок с фирменным персонажем XP-Pen лисенком Фениксом и чехол для пера)</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на 50%" +
                        " скидку на обучение в школе SkillBox по курсу «Профессия 2D-художник»</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">3 сертификата на" +
                        " 50% скидку на обучение в школе SkillBox по курсу «Профессия 3D-художник»</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDateTime.now())
                .archived(true)
                .build();

        News secondNews = News.builder()
                .title("Акция от AORUS: Играй и смотри!")
                .anons("Купите монитор и получите целый год фильмов с ivi и вкусную пиццу в подарок.")
                .fullText("<h2 style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"\"><font color=\"#ff0000\">" +
                        "Хорошие новости в Online-Shop!</font></b></h2><p style=\"margin-right: 0px; margin-bottom:" +
                        " 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;," +
                        " Arial, sans-serif;\"><span style=\"background-color: rgb(0, 255, 0);\">Смотреть кино стало" +
                        " еще интереснее и вкуснее.</span> При покупке одного из мониторов AORUS вы получаете в" +
                        " подарок 12 месяцев подписки на ivi и промокод на 1200 рублей в Додо-пицца. Акция продлится" +
                        " с 10 по 31 августа.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px;" +
                        " padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<i style=\"font-size: 1rem;\">Приятных покупок в Online-Shop!</i></p><p style=\"margin-right:" +
                        " 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\"><i style=\"font-size: 1rem;\">23<br></i><br></p>")
                .postingDate(LocalDateTime.now().minusDays(5L))
                .archived(false)
                .build();

        News thirdNews = News.builder()
                .title("Сегодня стартует предзаказ на флагманские продукты Samsung!")
                .anons("Сделайте предзаказ и получите подарок.")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDateTime.now().minusDays(13L))
                .archived(false)
                .build();

        News forthNews = News.builder()
                .title("Будь в плюсе вместе с нами!")
                .anons("Мы дарим дополнительный кэшбэк!")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Кэшбэк 3% — стандартные начисления и 7% — за онлайн-оплату!<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDateTime.now().minusDays(10L))
                .archived(false)
                .build();

        News fifthNews = News.builder()
                .title("Старт продаж Honor30i")
                .anons("Только у нас эксклюзивный смартфон!")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Супервыгода на HONOR 30i: получи кэшбэк 15% на свой Бонусный счёт<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDateTime.now().minusDays(1L))
                .archived(false)
                .build();

        News sixthNews = News.builder()
                .title("Отличная новость!")
                .anons("Online_store открывает продлёнку на скидки!")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Успей воспользоваться лучшим предложением, пока мы чистим стоки.<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDateTime.now().plusDays(30L))
                .archived(false)
                .build();

        newsService.save(firstNews);
        newsService.save(secondNews);
        newsService.save(thirdNews);
        newsService.save(forthNews);
        newsService.save(fifthNews);
        newsService.save(sixthNews);
    }

    /**
     * Метод первичного тестового заполнения товаров.
     */
    private void productInit() {

        Categories category1 = new Categories("Ноутбуки", "Компьютеры");
        Categories category2 = new Categories("Компьютеры", "Компьютеры");
        Categories category3 = new Categories("Смартфоны", "Смартфоны и гаджеты");
        Categories category4 = new Categories("Комплектующие", "Компьютеры");
        Categories category5 = new Categories("Периферия", "Компьютеры");
        Categories category6 = new Categories("Планшеты", "Смартфоны и гаджеты");
        Categories category7 = new Categories("Электронные книги", "Смартфоны и гаджеты");
        Categories category8 = new Categories("Аксессуары", "Смартфоны и гаджеты");
        Categories category9 = new Categories("Телевизоры", "ТВ и развлечения");
        Categories category10 = new Categories("Игры", "ТВ и развлечения");
        Categories category11 = new Categories("Аудиотехника", "ТВ и развлечения");
        Categories category12 = new Categories("Оргтехника", "Офис и сеть");
        Categories category13 = new Categories("Роутеры и сетевое оборудование", "Офис и сеть");
        Categories category14 = new Categories("Техника для кухни", "Бытовая техника");
        Categories category15 = new Categories("Техника для уборки", "Бытовая техника");
        Categories category16 = new Categories("Стиральные и сушильные машины", "Бытовая техника");
        Categories category17 = new Categories("Климатическая техника", "Бытовая техника");

        Product product1 = new Product("Asus-NX4567", 299.9, 15, 4.0, "Computer", false);
        Product product2 = new Product("ACER-543", 399.9, 10, 4.2, "Computer", false);
        Product product3 = new Product("Samsung-7893", 259.9, 20, 4.6, "Computer", false);

        Product product4 = new Product("NX-7893-PC-09878", 924.0, 3, 4.2, "Computer", false);
        Product product5 = new Product("ZX-7654-PC-1", 1223.9, 7, 4.7, "Computer", false);
        Product product6 = new Product("NY-2345-PC-453", 1223.9, 7, 4.7, "Computer", false);

        Product product7 = new Product("XIAOMI-Mi10", 599.9, 120, 4.9, "Cellphone", false);
        Product product8 = new Product("LG-2145", 439.5, 78, 3.9, "Cellphone", false);
        Product product9 = new Product("Apple-10", 1023.9, 74, 4.8, "Cellphone", false);

        Product product10 = new Product("Notebook 1", 99.9, 2, 0.0, "Computer");
        Product product11 = new Product("Notebook 2", 99.9, 2, 0.0, "Computer");
        Product product12 = new Product("Notebook 3", 99.9, 2, 0.0, "Computer");

        Product product13 = new Product("Roomba 698", 299.9, 6, 4.3, "Cleaning");
        Product product14 = new Product("Bosch BWD41720", 329.9, 8, 4.1, "Cleaning");
        Product product15 = new Product("Samsung SC4131", 69.9, 28, 4.6, "Cleaning");

        Product product16 = new Product("Samsung WW60K40G00W", 549.9, 3, 4.8, "Washing");
        Product product17 = new Product("Hotpoint-Ariston BI WDHG 75148 EU", 999.9, 2, 4.3, "Washing");
        Product product18 = new Product("Whirlpool TDLR 60111", 499.9, 6, 3.9, "Washing");

        Product product19 = new Product("Hotpoint-Ariston SPOWHA 409-K", 399.9, 2, 3.8, "Air_conditioner");
        Product product20 = new Product("LG P09EP2", 529.9, 2, 4.1, "Air_conditioner");
        Product product21 = new Product("LG Mega Plus P12EP1", 584.9, 2, 4.7, "Air_conditioner");

        Description description1 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "Оснащенный 15.6-дюймовым экраном ноутбук ASUS TUF Gaming FX505DT-AL087 – игровой портативный компьютер, который ничто не помешает вам использовать и в роли универсального домашнего компьютера.");
        Description description2 = new Description("23464223", "ACER", 1, "654x38x245", "yellow", 2.1, "some additional info here");
        Description description3 = new Description("99966732", "Samsung", 3, "550x27x368", "white", 1.1, "some additional info here");
        Description description4 = new Description("33311432NXU", "ATop corp.", 3, "698x785x368", "black", 3.1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris id condimentum tortor. Aliquam tristique tempus ipsum id laoreet. Pellentesque ligula lectus, finibus eget auctor pellentesque, molestie ac elit. Fusce in maximus leo. Morbi maximus vel enim");
        Description description5 = new Description("33211678NXU", "ATop corp.", 3, "690x765x322", "black", 3.5, "some additional info here");
        Description description6 = new Description("333367653Rh", "Rhino corp.", 3, "612x678x315", "orange", 2.8, "some additional info here");
        Description description7 = new Description("X54355543455", "Xiaomi", 1, "115x56x13", "grey", 0.115, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris id condimentum tortor. Aliquam tristique tempus ipsum id laoreet. Pellentesque ligula lectus, finibus eget auctor pellentesque, molestie ac elit. Fusce in maximus leo. Morbi maximus vel enim", 512, 512, "1920x960", true, "5.0");
        Description description8 = new Description("L55411165632", "LG", 2, "110x48x19", "black", 0.198, "some additional info here", 1024, 256, "1920x960", false, "4.0");
        Description description9 = new Description("A88563902273", "Apple corp.", 1, "112x55x8", "black", 0.176, "some additional info here", 2048, 128, "1024x480", true, "5.0");
        Description description10 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "Оснащенный 15.6-дюймовым экраном ноутбук ASUS TUF Gaming FX505DT-AL087 – игровой портативный компьютер, который ничто не помешает вам использовать и в роли универсального домашнего компьютера.");
        Description description11 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "Оснащенный 15.6-дюймовым экраном ноутбук ASUS TUF Gaming FX505DT-AL087 – игровой портативный компьютер, который ничто не помешает вам использовать и в роли универсального домашнего компьютера.");
        Description description12 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "Оснащенный 15.6-дюймовым экраном ноутбук ASUS TUF Gaming FX505DT-AL087 – игровой портативный компьютер, который ничто не помешает вам использовать и в роли универсального домашнего компьютера.");
        Description description13 = new Description("XYZ270011101230600001", "iRobot", 2, "300x75x300", "silver", 3.0, "Standard suction for an every day clean. Provides personalized cleaning suggestions.");
        Description description14 = new Description("CFE867594316856743201", "Bosch", 1, "360x350x490", "violet", 10.9, "Моющий пылесос Bosch BWD41720 — надежное устройство, позволяющее поддерживать чистоту напольных покрытий любого типа.");
        Description description15 = new Description("08UV8NEM703511M", "Samsung", 1, "365x230x275", "blue", 3.8, "Пылесос Samsung SC4131 используется для сухой уборки многокомнатных квартир и жилых домов.");
        Description description16 = new Description("X54355543455", "Samsung", 2, "850x600x450", "white", 54.0, "Позволяет бережно очищать от загрязнений одежду и текстильные изделия из хлопка, льна, синтетических волокон и деликатных тканей");
        Description description17 = new Description("A886UW16575632", "Whirlpool Corp.", 1, "815x595x540", "white", 65.0, "Встраиваемая стиральная машина способна за один цикл постирать и высушить до 7 кг вещей", 1024, 256, "1920x960", false, "4.0");
        Description description18 = new Description("A88563902273", "Whirlpool Corp.", 1, "900x420x600", "white", 49.0, "Автоматически определяется тип белья, расход воды и моющих средств. Устройство бережно относится к ткани и обеспечивает превосходный результат стирки.");
        Description description19 = new Description("AHP4388843455", "Whirlpool Corp.", 1, "270x835x210", "white", 6.5, "Кондиционер Hotpoint-Ariston SPOWHA 409-K используется для создания благоприятного микроклимата в помещениях площадью 27 м²");
        Description description20 = new Description("L856XZ11564632", "LG", 1, "265x756x184", "white", 7.4, "Кондиционер LG P09EP2 используется для установки оптимальной температуры в помещении дома или офиса площадью 20 м²");
        Description description21 = new Description("L014ZZ10018974", "LG", 1, "302x837x189; 483x717x230", "white", 8.7, "Модель LG Mega Plus P12EP1 будет оптимальна для установки в помещении площадью 35 м²");

        product1.setDescriptions(description1);
        product2.setDescriptions(description2);
        product3.setDescriptions(description3);
        product4.setDescriptions(description4);
        product5.setDescriptions(description5);
        product6.setDescriptions(description6);
        product7.setDescriptions(description7);
        product8.setDescriptions(description8);
        product9.setDescriptions(description9);
        product10.setDescriptions(description10);
        product11.setDescriptions(description11);
        product12.setDescriptions(description12);
        product13.setDescriptions(description13);
        product14.setDescriptions(description14);
        product15.setDescriptions(description15);
        product16.setDescriptions(description16);
        product17.setDescriptions(description17);
        product18.setDescriptions(description18);
        product18.setDescriptions(description19);
        product18.setDescriptions(description20);
        product18.setDescriptions(description21);

        category1.setProducts(Arrays.asList(product1, product2, product3, product10, product11, product12));
        category2.setProducts(Arrays.asList(product4, product5, product6));
        category3.setProducts(Arrays.asList(product7, product8, product9));
        category15.setProducts(Arrays.asList(product13, product14, product15));
        category16.setProducts(Arrays.asList(product16, product17, product18));
        category17.setProducts(Arrays.asList(product19, product20, product21));

        categoriesService.saveAll(Arrays.asList(category1, category2, category3,
                category4, category5, category6, category7, category8, category9, category10, category11,
                category12, category13, category14, category15, category16, category17));
    }

    /**
     * Метод первичного тестового заполнения заказов.
     */
    private void ordersInit() {
        User customer = userService.findByEmail("customer@mail.ru").get();

        List<Long> productsIds = new ArrayList<>();
        productsIds.add(productService.findProductByName("NX-7893-PC-09878").get().getId());
        productsIds.add(productService.findProductByName("Asus-NX4567").get().getId());
        productsIds.add(productService.findProductByName("ACER-543").get().getId());
        productsIds.add(productService.findProductByName("XIAOMI-Mi10").get().getId());
        productsIds.add(productService.findProductByName("LG-2145").get().getId());
        productsIds.add(productService.findProductByName("Apple-10").get().getId());
        productsIds.add(productService.findProductByName("Roomba 698").get().getId());
        productsIds.add(productService.findProductByName("Bosch BWD41720").get().getId());
        productsIds.add(productService.findProductByName("Hotpoint-Ariston BI WDHG 75148 EU").get().getId());
        productsIds.add(productService.findProductByName("LG Mega Plus P12EP1").get().getId());
        productsIds.add(productService.findProductByName("Hotpoint-Ariston SPOWHA 409-K").get().getId());

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(LocalDateTime.of(2019, 12, 31, 22, 10), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 1, 23, 13, 37), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 3, 10, 16, 51), Order.Status.INCARTS));
        orders.add(new Order(LocalDateTime.of(2020, 6, 13, 15, 3), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.of(2020, 7, 18, 16, 18), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 7, 24, 11, 9), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.of(2020, 8, 3, 15, 43), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 8, 18, 17, 33), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.of(2020, 9, 16, 10, 21), Order.Status.INCARTS));
        orders.add(new Order(LocalDateTime.now(), Order.Status.INCARTS));

        List<Long> ordersIds = new ArrayList<>();
        for (Order order : orders) {
            ordersIds.add(orderService.addOrder(order));
        }

        productInOrderService.addToOrder(productsIds.get(0), ordersIds.get(0), 1);
        productInOrderService.addToOrder(productsIds.get(1), ordersIds.get(0), 2);
        productInOrderService.addToOrder(productsIds.get(2), ordersIds.get(1), 1);
        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(2), 2);
        productInOrderService.addToOrder(productsIds.get(3), ordersIds.get(3), 1);
        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(3), 2);
        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(3), 3);
        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(4), 3);
        productInOrderService.addToOrder(productsIds.get(6), ordersIds.get(5), 1);
        productInOrderService.addToOrder(productsIds.get(6), ordersIds.get(6), 4);
        productInOrderService.addToOrder(productsIds.get(7), ordersIds.get(7), 1);
        productInOrderService.addToOrder(productsIds.get(8), ordersIds.get(8), 1);
        productInOrderService.addToOrder(productsIds.get(9), ordersIds.get(9), 2);
        productInOrderService.addToOrder(productsIds.get(10), ordersIds.get(9), 1);
        customer.setOrders(Set.copyOf(orderService.findAll()));
        userService.updateUser(customer);
    }

    /**
     * Метод первичного тестового заполнения акций.
     */
    private void stockInit() {
        Stock firstStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(12L))
                .stockTitle("Собери персональный компьютер на базе Intel® Core™ – получи скидку!")
                .stockText("оберите свой мощный компьютер на базе процессоров Intel® Core™! Корпуса,карты памяти, " +
                        "твердотельные накопители от именитых производителей, материнские платы MSI и процессоры " +
                        "Intel® Core™ помогут вам создать свою мощную машину! Работайте максимально эффективно на " +
                        "ПК с процессором Intel® Core™. Этот процессор обеспечивает впечатляющую производительность " +
                        "для развлечений и многозадачности. Улучшенная продуктивность, бесперебойная потоковая " +
                        "трансляция и превосходные развлечения в формате HD — это и многое другое с Intel® Core™! " +
                        "Используйте свое умное и продвинутое «железо» в работе и будьте эффективными и быстрыми в " +
                        "решении задач или же с азартом побеждайте врагов в «тяжелых» играх!" +
                        "Приобретая комплектующие для сборки ПК и процессоры Intel® Core™, вы получаете скидку 10 %!")
                .build();

        Stock secondStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(5L))
                .endDate(LocalDate.now().plusDays(3L))
                .stockTitle("Рассрочка или бонусы! Смартфоны Samsung Galaxy M-серии")
                .stockText("Смартфон Samsung Galaxy M21 обладает тройной камерой на 48+8+5 Мп, а M31 и M31s – " +
                        "квадрокамерами на 64+8+5+5 Мп и 64+12+5+5 соответственно. Такие параметры позволят вам " +
                        "совершенствовать мастерство в мобильной фотографии или видеосъемке в формате Ultra HD 4K." +
                        " Фронтальные камеры смартфонов порадуют любителей селфи – снимки будут получаться детальными" +
                        " и сочными. Galaxy M-серии работают с аккумуляторами емкостью 6 000 мА*ч. Система" +
                        " распознавания лица и сканер отпечатка пальца гарантируют сохранность ваших данных" +
                        " – доступ к информации будете иметь только вы. Выберите Samsung Galaxy M-серии," +
                        " отвечающий всем вашим требованиям." +
                        "Оформите беспроцентный кредит1 на смартфоны Samsung Galaxy M-серии из списка в" +
                        " любом магазине нашей сети или получите до 2 300 рублей на бонусную карту" +
                        " ProZaPass2 – выбор за вами!")
                .build();

        Stock thirdStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(20L))
                .endDate(LocalDate.now().minusDays(5L))
                .stockTitle("Скидки на игры ЕА!")
                .stockText("В течение действия акции вы можете приобрести игры ЕА из списка по" +
                        " очень привлекательным ценам!" +
                        "Вы можете стать обладателем игр EA для Xbox One, Nintendo Switch и PS4" +
                        " в различных жанрах. Ощутите всю радость победы в хоккейном матче, станьте" +
                        " стремительным уличным автогонщиком, постройте дом мечты или очутитесь в" +
                        " фантастическом мире и примите участие в битве галактических масштабов!")
                .build();

        Stock forthStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(10L))
                .stockTitle("«Рассрочка или бонусы!")
                .stockText("Стиральные машины Whirlpool помогут привести в порядок ваши вещи из различных тканей. " +
                        " Эта техника предлагает множество программ для деликатной и эффективной стирки и сушки." +
                        " Оформите беспроцентный кредит на бытовую технику Whirlpool или получите 10% от стоимости" +
                        " покупки на бонусную карту – выбор за вами!")
                .build();

        Stock fifthStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(10L))
                .stockTitle("«3 года защиты за 990")
                .stockText("Самое время обновить компьютер! Выбери подходящую модель с Windows 10 и добавь" +
                        " надёжную защиту от вирусов Kaspersky Internet Security на три года всего за 990 рублей." +
                        " Закажи компьютер с Windows 10. Забери товар и получи промокод на Kaspersky Internet Security" +
                        " за 990 рублей на свой Email и в личный кабинет в течение трёх дней после получения заказа." +
                        " Обязательно используй Бонусную карту – её можно оформить прямо на сайте.")
                .build();

        Stock sixthStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(10L))
                .stockTitle("«Требуй скидку!")
                .stockText("До 31 декабря требуй и получай скидку на смартфоны, телевизоры" +
                        " и бытовую технику из акционного списка." +
                        " Активируй свою скидку на странице товара." +
                        " Товары из акционного списка отмечены специальным знаком \"Требуй скидку!\" на сайте." +
                        " На товары из акционного перечня распространяются правила программы лояльности.")
                .build();

        stockService.addStock(firstStock);
        stockService.addStock(secondStock);
        stockService.addStock(thirdStock);
        stockService.addStock(forthStock);
        stockService.addStock(fifthStock);
        stockService.addStock(sixthStock);
    }

    public void sharedStockInit() {
        String[] socialNetworkNames = {"facebook", "vk", "twitter"};
        List<Stock> stocks = stockService.findAll();
        List<User> users = userService.findAll();
        Long firstNumber = stocks.get(0).getId();
        Long lastNumber = stocks.get(stocks.size() - 1).getId();
        Random random = new Random();
        for (Stock stock : stocks) {
            for (User user : users) {
                long generatedLongForStock = firstNumber + (long) (Math.random() * (lastNumber - firstNumber));
                SharedStock sharedStock = SharedStock.builder()
                        .user(user)
                        .stock(stockService.findStockById(generatedLongForStock))
                        .socialNetworkName(socialNetworkNames[random.nextInt(socialNetworkNames.length)])
                        .build();
                sharedStockService.addSharedStock(sharedStock);
            }
        }

    }

    /**
     * Метод первичного заполнения акций, которые были отправлены пользователям
     */
    public void sentStockInit() {
        Random random = new Random();
        List<Stock> stocks = stockService.findAll();
        List<User> users = userService.findAll();

        for (int i = 0; i < 20; i++) {
            sentStockService.addSentStock(SentStock.builder().user(users.get(random.nextInt(users.size())))
                    .stock(stocks.get(random.nextInt(stocks.size())))
                    .sentDate(LocalDate.now().plusDays(random.nextInt(8)))
                    .build());
        }
    }

    /**
     * Метод инициализации новостей и акций в профиле менеджера для тестирования динамической пагинации.
     */
    public void paginationNewsAndStocksInit() {
        for (int i = 0; i < 50; i++) {
            News news = News.builder()
                    .title(i + " Сегодня стартует предзаказ на флагманские продукты Samsung!")
                    .anons("Сделайте предзаказ и получите подарок.")
                    .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                            "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                            "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                            " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                            " font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b>" +
                            "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                            " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                            " font-size: 16px;\"><br></p></h1>")
                    .postingDate(LocalDateTime.now().minusDays(Math.round(Math.random() * 20)))
                    .archived(false)
                    .build();
            newsService.save(news);
        }

        for (int i = 0; i < 50; i++) {
            Stock stock = Stock.builder()
                    .startDate(LocalDate.now().minusDays(20L))
                    .endDate(LocalDate.now().minusDays(5L))
                    .stockTitle("Скидки на игры ЕА!")
                    .stockText("В течение действия акции вы можете приобрести игры ЕА из списка по" +
                            " очень привлекательным ценам!" +
                            "Вы можете стать обладателем игр EA для Xbox One, Nintendo Switch и PS4" +
                            " в различных жанрах. Ощутите всю радость победы в хоккейном матче, станьте" +
                            " стремительным уличным автогонщиком, постройте дом мечты или очутитесь в" +
                            " фантастическом мире и примите участие в битве галактических масштабов!")
                    .build();
            stockService.addStock(stock);
        }
    }
}
