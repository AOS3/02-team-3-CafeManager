package com.lion.cafemanager_1.util

// 프래그먼트를 나타내는 값
enum class FragmentName(var number:Int, var str:String){
    // 로그인 화면
    LOGIN_FRAGMENT(1, "LoginFragment"),
    // 비밀번호 화면 설정 화면
    SETTING_PASSWORD_FRAGMENT(2, "SettingPasswordFragment"),
    // 첫 화면
    MAIN_FRAGMENT(3, "MainFragment"),
    // 입력 화면
    INPUT_FRAGMENT(4, "InputFragment"),
    // 출력 화면
    SHOW_FRAGMENT(5, "ShowFragment"),
    // 수정 화면
    MODIFY_FRAGMENT(6, "ModifyFragment"),
    // 검색 화면
    SEARCH_FRAGMENT(7, "SearchFragment"),
    // 목록 화면
    SHOW_ALL_FRAGMENT(8, "ShowAllFragment"),
    // 커피 메뉴 화면
    COFFEE_FRAGMENT(9, "CoffeeFragment"),
    // 논커피 메뉴 화면
    NON_COFFEE_FRAGMENT(10, "NonCoffeeFragment"),
    // 빵 메뉴 화면
    BREAD_FRAGMENT(11, "BreadFragment"),
    // 케이크 메뉴 화면
    CAKE_FRAGMENT(12, "CakeFragment"),
    // 케이크 예약 메뉴 화면
    CAKE_BOOKING_FRAGMENT(13, "CakeBookingFragment")
}

// 카페 음료 및 음식 타입을 나타내는 값
enum class CafeType(var number:Int, var str:String){
    // 커피
    CAFE_TYPE_COFFEE(1, "커피"),
    // 논커피
    CAFE_TYPE_NONCOFFEE(2, "논커피"),
    // 빵
    CAFE_TYPE_BREAD(3, "빵"),
    // 케이크
    CAFE_TYPE_CAKE(4, "케이크")
}

// 카페 음료 사이즈를 나타내는 값
enum class CafeSize(var number:Int, var str:String){
    // 라지
    CAFE_SIZE_LARGE(1, "라지"),
    // 미디움
    CAFE_SIZE_MEDIUM(2, "미디움"),
    // 스몰
    CAFE_SIZE_SMALL(3, "스몰"),
    // 해당 없음
    CAFE_SIZE_NO(4, "해당 없음")
}

// 카페 음식 포장 가능 유무
enum class CafeTakeOut(var number: Int, var str: String){
    // 포장 가능
    CAFE_TAKEOUT_OK(1, "포장 가능"),
    // 포장 불가능
    CAFE_TAKEOUT_NO(2, "포장 불가능")
}

fun numberToCafeType(cafeType:Int) = when(cafeType){
    1 -> CafeType.CAFE_TYPE_COFFEE
    2 -> CafeType.CAFE_TYPE_NONCOFFEE
    3 -> CafeType.CAFE_TYPE_BREAD
    else -> CafeType.CAFE_TYPE_CAKE
}

fun numberToCafeSize(cafeSize:Int) = when(cafeSize){
    1 -> CafeSize.CAFE_SIZE_LARGE
    2 -> CafeSize.CAFE_SIZE_MEDIUM
    3 -> CafeSize.CAFE_SIZE_SMALL
    else -> CafeSize.CAFE_SIZE_NO
}

fun numberToCafeTakeOut(cafeTakeOut:Int) = when(cafeTakeOut){
    1 -> CafeTakeOut.CAFE_TAKEOUT_OK
    else -> CafeTakeOut.CAFE_TAKEOUT_NO
}