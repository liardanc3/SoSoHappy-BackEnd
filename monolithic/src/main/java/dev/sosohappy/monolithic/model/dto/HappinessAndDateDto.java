package dev.sosohappy.monolithic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import dev.sosohappy.monolithic.model.entity.*;
import java.text.DateFormatSymbols;
import java.util.Locale;

@Data
@AllArgsConstructor
public class HappinessAndDateDto {

    private Double happiness;
    private String formattedDate;

    public HappinessAndDateDto(Feed feed){
        this.happiness = feed.getHappiness().doubleValue();
        this.formattedDate = feed.getDate().toString().substring(6,8);

        if(this.formattedDate.charAt(0) == '0'){
            this.formattedDate = this.formattedDate.substring(1);
        }
    }

    public HappinessAndDateDto(Double happiness, Long date) {
        this.happiness = happiness;
        this.formattedDate = new DateFormatSymbols(Locale.ENGLISH).getShortMonths()[Integer.parseInt(date.toString().substring(4, 6)) - 1];
    }
}
