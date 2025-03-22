package com.example.footzone;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class SelectTeamActivity extends AppCompatActivity {

    private Spinner teamSpinner;
    private Button selectButton;
    private TextView teamInfo;

    private String[] teams = {
            "Барселона", "Реал Мадрид", "Атлетико Мадрид", "Манчестер Сити", "Манчестер Юнайтед",
            "Арсенал", "Ливерпуль", "Челси", "Бавария", "Интер", "Ювентус", "Рома", "ПСЖ"
    };

    private Map<String, String> teamInfoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);

        teamSpinner = findViewById(R.id.team_spinner);
        selectButton = findViewById(R.id.select_button);
        teamInfo = findViewById(R.id.team_info);

        // Настройка Spinner с командами
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teams);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(adapter);

        // Информация о командах
        teamInfoMap = new HashMap<>();
        teamInfoMap.put("Барселона", "Барселона - основана в 1899 году. Клуб выиграл 5 Лиг чемпионов, 26 титулов чемпионов Испании и 31 Кубок Короля. Знаменитые игроки: Лионель Месси, Хави, Андрес Иньеста.");
        teamInfoMap.put("Реал Мадрид", "Реал Мадрид - основан в 1902 году. Клуб выигрывал 14 Лиг чемпионов, 35 титулов чемпионов Испании и 19 Кубков Короля. Знаменитые игроки: Криштиану Роналду, Альфредо Ди Стефано, Зинедин Зидан.");
        teamInfoMap.put("Атлетико Мадрид", "Атлетико Мадрид - основан в 1903 году. Клуб выигрывал 11 титулов чемпионов Испании, 10 Кубков Короля и 3 Лиги Европы. Знаменитые игроки: Диего Форлан, Антуан Гризманн.");
        teamInfoMap.put("Манчестер Сити", "Манчестер Сити - основан в 1880 году. Клуб выиграл 9 титулов чемпионов Англии, 6 Кубков Англии и 1 Лигу чемпионов. Знаменитые игроки: Серхио Агуэро, Кевин Де Брюйне.");
        teamInfoMap.put("Манчестер Юнайтед", "Манчестер Юнайтед - основан в 1878 году. Клуб выиграл 20 титулов чемпионов Англии, 12 Кубков Англии и 3 Лиги чемпионов. Знаменитые игроки: Эрик Кантона, Райан Гиггз, Руни.");
        teamInfoMap.put("Арсенал", "Арсенал - основан в 1886 году. Клуб выигрывал 13 титулов чемпионов Англии, 14 Кубков Англии. Знаменитые игроки: Тьерри Анри, Деннис Беркамп.");
        teamInfoMap.put("Ливерпуль", "Ливерпуль - основан в 1892 году. Клуб выиграл 6 Лиг чемпионов, 19 титулов чемпионов Англии, 8 Кубков Англии. Знаменитые игроки: Стивен Джеррард, Кенни Далглиш.");
        teamInfoMap.put("Челси", "Челси - основан в 1905 году. Клуб выиграл 6 титулов чемпионов Англии, 2 Лиги чемпионов и 8 Кубков Англии. Знаменитые игроки: Фрэнк Лэмпард, Джон Терри.");
        teamInfoMap.put("Бавария", "Бавария - основана в 1900 году. Клуб выиграл 6 Лиг чемпионов, 32 титула чемпионов Германии и 20 Кубков Германии. Знаменитые игроки: Франц Беккенбауэр, Герд Мюллер.");
        teamInfoMap.put("Интер", "Интер - основан в 1908 году. Клуб выиграл 3 Лиги чемпионов, 19 титулов чемпионов Италии и 7 Кубков Италии. Знаменитые игроки: Джузеппе Меацца, Роналдо.");
        teamInfoMap.put("Ювентус", "Ювентус - основан в 1897 году. Клуб выиграл 2 Лиги чемпионов, 36 титулов чемпионов Италии и 14 Кубков Италии. Знаменитые игроки: Алессандро Дель Пьеро, Джанлуиджи Буффон.");
        teamInfoMap.put("Рома", "Рома - основана в 1927 году. Клуб выигрывал 3 титула чемпионов Италии, 9 Кубков Италии и 1 Кубок чемпионов. Знаменитые игроки: Франческо Тотти, Даниэле Де Росси.");
        teamInfoMap.put("ПСЖ", "ПСЖ - основан в 1970 году. Клуб выиграл 1 Лигу чемпионов, 9 титулов чемпионов Франции и 14 Кубков Франции. Знаменитые игроки: Неймар, Килиан Мбаппе.");


        // Обработка выбора команды
        selectButton.setOnClickListener(v -> {
            String selectedTeam = teamSpinner.getSelectedItem().toString();
            String info = teamInfoMap.get(selectedTeam);
            teamInfo.setText(info);
        });
    }
}
