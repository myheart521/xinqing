package com.hpu.xinqing.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.check.WordChecks;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.resultcondition.WordResultConditions;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class SensitiveWordConfig {

    @Bean
    public SensitiveWordBs getSensitiveWordBs(){
        SensitiveWordBs wordBs = SensitiveWordBs.newInstance()
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(true)
                .enableNumCheck(true)
                .enableEmailCheck(true)
                .enableUrlCheck(true)
                .enableIpv4Check(true)
                .enableWordCheck(true)
                .wordFailFast(true)
                .wordCheckNum(WordChecks.num())
                .wordCheckEmail(WordChecks.email())
                .wordCheckUrl(WordChecks.url())
                .wordCheckIpv4(WordChecks.ipv4())
                .wordCheckWord(WordChecks.word())
                .numCheckLen(8)
                .numCheckLen(9)
                .numCheckLen(10)
                .numCheckLen(11)
                .wordTag(WordTags.none())
                .charIgnore(SensitiveWordCharIgnores.defaults())
                .wordResultCondition(WordResultConditions.alwaysTrue())
                .init();
        return wordBs;
    }

}
