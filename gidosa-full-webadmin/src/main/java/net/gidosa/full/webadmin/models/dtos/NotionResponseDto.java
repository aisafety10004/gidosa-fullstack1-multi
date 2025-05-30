package net.gidosa.full.webadmin.models.dtos;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 노션 API 응답을 담는 DTO 클래스
 */
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotionResponseDto {
    /** 응답 객체 타입 (예: "list") */
    private String object;

    /** 페이지 결과 목록 */
    private List<NotionPage> results;

    /** 페이지네이션을 위한 다음 커서 */
    private String nextCursor;

    /** 추가 결과 존재 여부 */
    private boolean hasMore;

    /** 응답 타입 (예: "page_or_database") */
    private String type;

    /** 페이지 또는 데이터베이스 정보 */
    private Map<String, Object> pageOrDatabase;

    /** 개발자 설문 URL */
    private String developerSurvey;

    /** 요청 ID */
    private String requestId;

    /**
     * 노션 페이지 정보를 담는 내부 클래스
     */
    @Data
    public static class NotionPage {
        /** 페이지 객체 타입 */
        private String object;

        /** 페이지 고유 ID */
        private String id;

        /** 페이지 생성 시간 */
        private String createdTime;

        /** 페이지 마지막 수정 시간 */
        private String lastEditedTime;

        /** 페이지 생성자 정보 */
        private NotionUser createdBy;

        /** 페이지 마지막 수정자 정보 */
        private NotionUser lastEditedBy;

        /** 페이지 커버 이미지 */
        private Object cover;

        /** 페이지 아이콘 */
        private Object icon;

        /** 페이지의 부모 정보 (데이터베이스 등) */
        private NotionParent parent;

        /** 페이지 보관 여부 */
        private boolean archived;

        /** 휴지통 여부 */
        private boolean inTrash;

        /** 페이지 속성들 (제목, 태그, 날짜 등) */
        private Map<String, NotionProperty> properties;

        /** 페이지 URL */
        private String url;

        /** 공개 URL */
        private String publicUrl;
    }

    /**
     * 노션 사용자 정보를 담는 내부 클래스
     */
    @Data
    public static class NotionUser {
        /** 사용자 객체 타입 */
        private String object;

        /** 사용자 ID */
        private String id;
    }

    /**
     * 노션 페이지의 부모 정보를 담는 내부 클래스
     */
    @Data
    public static class NotionParent {
        /** 부모 타입 (예: "database_id") */
        private String type;

        /** 데이터베이스 ID */
        private String databaseId;
    }

    /**
     * 노션 페이지의 속성을 담는 내부 클래스
     */
    @Data
    public static class NotionProperty {
        /** 속성 ID */
        private String id;

        /** 속성 타입 (예: "title", "select", "date") */
        private String type;

        /** 선택형 속성 값 */
        private Object select;

        /** 날짜형 속성 값 */
        private Object date;

        /** 제목형 속성 값 */
        private List<NotionTitle> title;
    }

    /**
     * 노션 제목 속성의 상세 정보를 담는 내부 클래스
     */
    @Data
    public static class NotionTitle {
        /** 제목 타입 */
        private String type;

        /** 제목 텍스트 정보 */
        private NotionText text;

        /** 텍스트 스타일 정보 */
        private NotionAnnotations annotations;

        /** 일반 텍스트 */
        private String plainText;

        /** 하이퍼링크 */
        private String href;
    }

    /**
     * 노션 텍스트 정보를 담는 내부 클래스
     */
    @Data
    public static class NotionText {
        /** 텍스트 내용 */
        private String content;

        /** 링크 URL */
        private String link;
    }

    /**
     * 노션 텍스트 스타일 정보를 담는 내부 클래스
     */
    @Data
    public static class NotionAnnotations {
        /** 굵게 */
        private boolean bold;

        /** 기울임 */
        private boolean italic;

        /** 취소선 */
        private boolean strikethrough;

        /** 밑줄 */
        private boolean underline;

        /** 코드 */
        private boolean code;

        /** 텍스트 색상 */
        private String color;
    }
}
