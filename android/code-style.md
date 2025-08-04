# Kotlin / Android Platform

[Resource](https://www.notion.so/Resource-22ba540dc0b780618aedee48bdb3ec5b?pvs=21)

[Test Convention](https://www.notion.so/Test-Convention-22ba540dc0b7809d8712d2a831e04396?pvs=21)

# 📝 안드로이드 개발 컨벤션 가이드라인

본 컨벤션 문서는 [PRNDcompany/android-style-guide](https://github.com/PRNDcompany/android-style-guide)
의 [Kotlin.md](https://github.com/PRNDcompany/android-style-guide/blob/main/Kotlin.md)
및 [Resource.md](https://github.com/PRNDcompany/android-style-guide/blob/main/Resource.md)를 기반으로
작성되었습니다.

## 1. 일반 원칙

- **기존 컨벤션 유지**: 현재 문서에 명시된 모든 기존 컨벤션은 어떠한 경우에도 변경할 수 없습니다.
- **신규 컨벤션 추가**: 프로젝트 진행 중 필요한 경우 새로운 컨벤션을 논의하고 본 문서에 추가할 수 있습니다.

---

## 2. Kotlin 코드 컨벤션

### 2.1. Boolean 비교

- `if (a?.b?.isTraded ?: false)` 방식보다는 `if (a?.b?.isTraded == true)` 와 같은 방식으로 명시적인 비교를 선호합니다.

### 2.2. Custom Accessor vs Function

- *행동(Action)**을 나타내는 개념이라면 `function`으로 정의합니다.
- *상태나 값 등 정보(State/Value)**를 가져오는 개념이라면 `custom accessor`(속성)로 정의합니다.
    -
    참조: [Kotlin: should I define Function or Property?](https://www.google.com/search?q=https://kotlinlang.org/docs/coding-conventions.html%23functions-vs-properties)

### 2.4. Naming Rules

### 2.4.1. Package 이름

- package 이름은 **소문자**로 작성합니다.
    - 예시: `package kr.co.prnd.domain`
- **underscore(`_`)는 사용하지 않습니다.**
    - `//warning 
    package kr.co.prnd.domain_module`
- 예외적으로 불가피하게 연결된 단어를 붙여서 사용해야 하는 경우에는 **camelCase**로 처리합니다.
    - 예시: `package com.example.myProject`

### 2.4.2. LiveData 변수명

- XML에서 클릭 이벤트 시 사용되는 `LiveData`의 변수명은 `xxxEvent`로 선언합니다.
    -
    참조: [Google Android Jetpack Compose samples (Blueprint)](https://www.google.com/search?q=https://github.com/android/architecture-samples/blob/dev-dagger/app/src/main/java/com/example/android/architecture.samples/tasks/TasksViewModel.kt%23L106)

### 2.4.3. 함수 이름

- **ViewModel을 `observe()`할 때 모아 놓는 함수 이름**:
    - `setupXXX()`
- **서버에서 데이터를 불러올 때 함수 이름**:
    - `fetchXXX()`
- **서버에 데이터를 저장할 때 함수 이름**:
    - `saveXXX()`
- **Return 값이 있는 데이터를 불러올 때 함수 이름**:
    - `getXXX()`
- **특정 객체를 찾는 함수 이름**:
    - `findXXX()`
- **복수형 데이터를 가져올 때는 뒤에 `s`를 붙입니다.**
    - `getBrands()` // O
    - `getBrandList()` // X
- **Raw 값으로부터 `enum`을 찾을 때 함수 이름은 `find()`로 합니다.**

    ```kotlin
    enum class Color {
        RED, BLUE, GREEN;
    
        fun find(rawColor: String): Color = when (rawColor) {
            "red" -> RED
            "blue" -> BLUE
            "green" -> GREEN
            else -> throw IllegalArgumentException("invalid color: $rawColor")
        }
    }
    ```

### 2.5. Listener Naming

### 2.5.1. Listener 인터페이스 이름

- `function`을 1개만 가진 경우: `fun interface OnXXXXListener`
- `function`을 2개 이상 가진 경우: `interface XXXListener`

### 2.5.2. `on[명사][동사]()` (현재형)

- **Publisher(이벤트 발생 주체)가 이벤트만 전달하고 Listener가 전적인 책임을 처리할 때 사용합니다.**
- 이벤트를 `handle`하는 주체가 `listen`하고 있는 곳일 때 사용합니다.
    - 예시: `fun onClick()`, `fun onFocusChange()`, `fun onScrollChange()`, `fun onAnimationStart()`,
      `fun onTextChange()`

### 2.5.3. `on[명사][동사 과거형]()`

- **Publisher가 무언가를 처리하고 Listener에게 해당 동작이 완료되었음을 알릴 때 사용합니다.**
- 어떤 동작을 하고 나서 이 동작이 일어났음을 Listener에게 알려줄 때 사용합니다.
- `onEach()`, `doOnXXX()` 개념처럼 특정 이벤트를 `intercept`해서 쓸 때 사용합니다.
- 동작을 한 뒤에 Listener를 호출해야 과거형의 이름과 일치합니다.
    - 예시: `fun onScrollStateChanged()`, `fun onTextChanged()`

### 2.5.4. 기타 Listener 규칙

- Listener를 구현하는 곳에서 과거형 여부에 따라, 해당 이벤트에 대한 처리를 해야 하는지 말아야 하는지를 판단할 수 있습니다.

### 2.6. Formatting

### 2.6.1. 개행

- 생성자, 함수에서 Parameter를 정의할 때 한 줄로 정의 가능하면 한 줄로 작성합니다.
- 한 줄로 정의하기 어렵다면 각 parameter별로 개행합니다.

### 2.6.2. `When` Statement

- 한 줄에 들어가는 `when` 분기는 중괄호(`{}`)를 사용하지 않습니다.

    ```kotlin
    when (value) {
        0 -> return
        // ...
    }
    ```

- 여러 개의 조건을 동시에 사용하는 경우 `>`를 포함한 블록은 다음 줄로 내려서 작성합니다.

    ```kotlin
    when (value) {
        foo -> // ...
        bar,
        baz     -> return
    }
    ```

---

## 3. Android Resource 컨벤션

### 3.1. Layout

### 3.1.1. Layout 파일 이름 (`<WHAT>_<WHERE>`)

| WHAT Prefix | 설명                                                                |
|-------------|-------------------------------------------------------------------|
| `activity_` | `Activity`에서 쓰이는 layout                                           |
| `fragment_` | `Fragment`에서 쓰이는 layout                                           |
| `dialog_`   | `Dialog`에서 쓰이는 layout                                             |
| `view_`     | `CustomView`에서 쓰이는 layout                                         |
| `item_`     | `RecyclerView`, `GridView`, `ListView` 등 `ViewHolder`에 쓰이는 layout |
| `layout_`   | `<include/>`로 재사용되는 공통의 layout                                    |

- **예시**:
    - `activity_main.xml`: `MainActivity`의 layout
    - `fragment_request.xml`: `RequestFragment`의 layout
    - `dialog_contact.xml`: 문의안내 Dialog의 layout
    - `view_rating.xml`: 커스텀으로 만든 `RatingView`의 layout
    - `item_my_car.xml`: 내 차량 목록에서 사용되는 각각의 item의 layout
    - `layout_dealer_review.xml`: 재사용되는 딜러 리뷰 layout

### 3.1.2. ID (`<WHAT>_<DESCRIPTION>`)

- View의 대문자를 축약하여 `<WHAT>`의 Prefix로 사용합니다.
- 아래 이름 규칙을 적용합니다.
    - Android의 `View`는 CamelCase의 대문자를 축약한 형태로 정합니다.
        - 예시: `TextView` -> `tv_`
    - 만약 `View`의 이름이 `Space`, `Switch`와 같이 1개의 대문자만 존재한다면 모두 소문자인 아이디로 정합니다.
        - 예시: `Switch` -> `switch_`
    - `CustomView`는 전체 View의 이름을 snake_case 이름으로 정합니다.
        - 예시: `MyCustomView` -> `my_custom_view`
        - (만약 1개의 xml에 같은 여러 `CustomView`가 존재한다면 `<WHAT>_<DESCRIPTION>`의 형태로 정합니다.)
    - 아래 표에 해당 View의 Prefix가 정의되어 있지 않다면 팀에서 상의하여 이름을 정한 뒤 추가합니다.

        | View | Prefix |
        | --- | --- |
        | `TextView` | `tv_` |
        | `ImageView` | `iv_` |
        | `CheckBox` | `cb_` |
        | `RecyclerView` | `rv_` |
        | `EditText` | `et_` |
        | `ProgressBar` | `pb_` |
        | `FrameLayout` | `fl_` |
        | `NestedScrollView` | `nsv_` |
        | `Space` | `space_` |
        | `Switch` | `switch_` |
        | `AbcDeFgh` | `adf_` |
        | `Abcdef` | `abcdef_` |
        | `MyCustomView` | `my_custom_view` |
        | `YourView` | `your_view` |

- **기타**:
    - 해당 View를 특정 기능과 상관없이 `VISIBLE`/`GONE` 등의 View의 용도로 사용한다면 `view_xxx`로 사용하는 것도 허용합니다.
    - 버튼 기능을 위한 View는 `ImageView`, `TextView`로만 사용합니다. (`Button`, `ImageButton`은 존재의 의미가 없음)
- **예시**:
    - `iv_close`: 닫기 `ImageView`
    - `tv_select`: 선택 `TextView`
    - `rv_car_list`: 자동차 목록 `RecyclerView`
    - `view_etc_model`: 기타 모델 화면 `LinearLayout`

### 3.2. Drawable

### 3.2.1. Drawable 파일 이름 (`<WHAT>(_<WHERE>)_<DESCRIPTION>(_<SIZE>)`)

- 이미지가 여러 군데에서 활용될 경우, `<WHERE>`는 생략 가능합니다.
- 이미지의 크기가 1개밖에 없는 경우, `<SIZE>`는 생략 가능합니다.

| What Prefix | 설명                               |
|-------------|----------------------------------|
| `ic_`       | 버튼이 아닌 화면에 보여지는 이미지 (아이콘 형태)     |
| `bg_`       | 버튼이 아닌 화면에 보여지는 이미지 (배경 형태)      |
| `img_`      | 실제 사진이거나 아이콘 형태가 아닌 일러스트 형태의 이미지 |
| `div_`      | `divider`로 활용되는 이미지              |

### 3.2.2. Selector 이름

- 배경이나 버튼에서 View의 상태에 따라 drawable이 변해야 하는 경우의 이름은 아래와 같습니다.

| 상태 Suffix   | 설명          |
|-------------|-------------|
| `_normal`   | Normal 상태   |
| `_pressed`  | Pressed 상태  |
| `_focused`  | Focused 상태  |
| `_disabled` | Disabled 상태 |
| `_selected` | Selected 상태 |

### 3.2.3. Background Drawable 이름

- 배경색이 pressed 상태에 따라 white -> sky_blue로 변하는 경우: `bg_white_to_sky_blue.xml`
- 배경이 white 색의 24dp로 테두리를 그리는 경우: `bg_white_radius_24dp.xml`
- 배경이 투명하며 배경의 선만을 sky_blue 색의 8dp로 테두리를 그리는 경우: `bg_stroke_sky_blue_radius_8dp.xml`

### 3.2.4. 기타 Drawable 규칙

- `img_xxx`의 경우 파일의 크기가 큰 경우가 많으므로 `tinypng`에서 파일 크기를 줄인 뒤에 추가해야 합니다. (GitHub `imgbot`을 사용한다면 생략 가능)
- 대부분 용량이 큰 파일이어서 `xxxhdpi`에만 넣습니다.
- **예시**:
    - `btn_call_normal.png`: 전화 걸기 버튼 이미지
    - `btn_call_pressed.png`: 전화 걸기 버튼 눌렸을 때의 이미지
    - `btn_call.xml`: 전화 걸기 버튼 이미지의 selector xml
    - `ic_dealer_gift.png`: 딜러가 보내준 기프티콘을 보여줄 때 표시되는 이미지
    - `img_splash_chart.png`: 스플래시 화면에서 보여지는 차트 이미지

### 3.3. Dimension

3.3.1. Dimension 이름

- 여러 군데에서 재사용되는 개념이라면 변수로 정의하여 `@dimen/xxx`와 같이 사용합니다.
- 그렇지 않다면 명시적으로 `16dp`와 같이 XML 코드에 직접 작성합니다.

### 3.3.2. Margin/Padding4

- 대부분의 margin/padding은 아래 정의된 `space_xxx`로만 사용되도록 합니다.
    - `<dimen name="space_small">12dp</dimen>`
    - `<dimen name="space_median">16dp</dimen>`
    - `<dimen name="space_s_large">18dp</dimen>`
    - `<dimen name="space_large">20dp</dimen>`
    - `<dimen name="space_x_large">24dp</dimen>`
- 그 외에 특정 화면에서 위의 값을 따르지 않는 경우, `<WHERE>_<DESCRIPTION>_<WHAT>`의 규칙으로 만듭니다.
    - 예시:
        - `<dimen name="register_car_item_car_model_start_padding">40dp</dimen>`
        - `<dimen name="register_car_item_grade_start_padding">56dp</dimen>`
        - `<dimen name="register_car_item_car_detail_start_padding">72dp</dimen>`
- 2번 이상 쓰이는 경우는 `dimen`에 정의하는 것을 강제하고, 1번만 쓰이는 경우에는 XML 코드에 직접 넣어도 괜찮습니다.

### 3.3.3. Height/Size

- 높이만 지정할 때는 `height`, 1:1 비율로 같은 값이 들어갈 때는 `size`로 합니다.
    - 예시:
        - `<dimen name="toolbar_height">56dp</dimen>`
        - `<dimen name="register_input_view_default_height">280dp</dimen>`
        - `<dimen name="register_input_view_collapse_height">200dp</dimen>`
        - `<dimen name="dealer_profile_image_size">48dp</dimen>`

### 3.4. String

### 3.4.1. String 이름 (`<WHERE>_<DESCRIPTION>`)

- 특정 화면에서 쓰이는 텍스트가 아니라 여러 군데에서 공통으로 재사용될 텍스트라면 `all_<DESCRIPTION>`으로 이름을 짓습니다.
- **예시**:
    - `permission_dialog_camera_title`: 카메라 권한을 요구하는 Dialog의 제목
    - `permission_dialog_camera_description`: 카메라 권한을 요구하는 Dialog의 설명 내용
    - `all_yes`: 네
    - `all_ok_understand`: 여러 Dialog에서 네, 알겠습니다로 쓰이는 공통의 텍스트

### 3.4.2. 문단

- 문단 형태의 긴 문자열로 개행(`\n`)이 필요한 경우, `\n`을 다음 줄의 앞에 씁니다.

    ```kotlin
    <string name="sample">문단 첫번째줄
        \\n문단 두번째줄
        \\n문단 세번째줄</string>
    ```

### 3.5. Theme/Style

### 3.5.1. 파일 위치 및 사용 규칙

- `Theme`는 `themes.xml`, `Style`은 `styles.xml`에 추가합니다.
- 1번만 쓰이는 경우에는 `style`을 만들지 않습니다. (단, 앞으로 재사용될 가능성이 높은 경우에는 가능)
- 모든 `style`은 `parent`를 갖습니다.

### 3.5.2. Naming

- `style`의 이름은 `parent`의 이름 패턴과 맞춥니다.

    ```kotlin
    <style name="Widget.HeyDealer.Button" parent="@style/Widget.AppCompat.Button">
    </style>
    ```

- `parent`에서 일부 내용만 수정하고자 하는 경우, `parent` 이름 뒤에 달라진 내용의 내용을 추가해줍니다.

    ```kotlin
    <style name="Theme.HeyDealer.Transparent" parent="Theme.HeyDealer">
    </style>
    ```

- `Base Style`과 `Theme`의 경우는 앞에 `Base`를 붙입니다.XML

    ```kotlin
    <style name="Base.Theme" parent="..." />
    <style name="Base.Theme.Transparent">...</style>
    
    <style name="HeyDealerTheme" parent="Base.Theme">...</style>
    <style name="HeyDealerTheme.Transparent" parent="Base.Theme.Transparent" />
    
    <style name="Base.TextAppearance.HeyDealer" parent="...">...</style>
    <style name="Base.TextAppearance.HeyDealer.Headline">...</style>
    <style name="TextAppearance.HeyDealer.Headline1" parent="Base.TextAppearance.HeyDealer.Headline">...</style>
    <style name="TextAppearance.HeyDealer.Headline2" parent="Base.TextAppearance.HeyDealer.Headline">...</style>
    ```

### 3.5.3. Attribute

- Attribute 이름은 **camelCase**로 합니다.XML

    ```kotlin
    <attr name="numStars" format="integer" />
    ```

- 기존에 정의되어 있는 `android:xxx`와 같은 동작을 유도하는 경우, 이 태그를 재사용합니다.XML

    ```kotlin
    <declare-styleable name="SpannedGridLayoutManager">
        <attr name="android:orientation" />
        </declare-styleable>
    ```

### 3.6. 기타 Resource 규칙

- `android:xxxLeft`/`android:xxxRight` 대신 **`android:xxxStart`/`android:xxxEnd`*를 사용합니다. (모든
  Left/Right 사용 부분에 적용)
