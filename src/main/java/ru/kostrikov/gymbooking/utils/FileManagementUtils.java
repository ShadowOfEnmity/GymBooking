package ru.kostrikov.gymbooking.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileManagementUtils {
   public String generateNewImageName(String path) {
        if (path !=null && !path.isBlank()) {
            String regex = "(.*/)([^/]+)\\.(\\w+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(path);
            if (matcher.find()) {
                return new StringBuilder().append(matcher.group(1)).append(UUID.randomUUID()).append(".").append(matcher.group(3)).toString();
            }
        }
        return "";
    }
}
