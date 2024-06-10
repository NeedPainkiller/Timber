package xyz.needpainkiller.api.tenant.adapter.in.web.data;


/**
 * 테넌트 생성/수정 요청 인터페이스
 *
 * @author needpainkiller6512
 * @version 1.0
 */
public interface UpsertTenantRequest {
    void setUrl(String url);

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    Boolean getVisibleYn();

    String getTitle();

    String getLabel();

    String getUrl();

    void setVisibleYn(Boolean visibleYn);

    void setTitle(String title);

    void setLabel(String label);

}