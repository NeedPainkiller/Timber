package xyz.needpainkiller.api.team.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import xyz.needpainkiller.api.tenant.domain.model.TenantBase;
import xyz.needpainkiller.lib.jpa.BooleanConverter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(value = {"hibernate_lazy_initializer", "handler"}, ignoreUnknown = true)
@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "TEAM")
public class Team implements Serializable, TenantBase {
    @Serial
    private static final long serialVersionUID = -1218478122591768291L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_ID", unique = true, nullable = false)
    private Long id;
    @Column(name = "TENANT_PK", nullable = false, columnDefinition = "bigint default 0")
    private Long tenantPk;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "USE_YN", nullable = false, columnDefinition = "tinyint unsigned default 0")
    @ColumnDefault("0")
    private boolean useYn;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "VISIBLE_YN", nullable = false, columnDefinition = "tinyint unsigned default 0")
    @ColumnDefault("0")
    private Boolean visibleYn;
    @Column(name = "TEAM_NAME", nullable = false, columnDefinition = "nvarchar(256)")
    private String teamName;
    @Column(name = "TEAM_ORDER", nullable = false, columnDefinition = "int unsigned default 9999")
    private Integer order;

    @Convert(converter = TeamLevel.Converter.class)
    @Column(name = "TEAM_LEVEL", nullable = false, columnDefinition = "int unsigned default 0")
    private TeamLevel teamLevel;
    @Column(name = "TEAM_PATH", nullable = true, columnDefinition = "nvarchar(2048)")
    private String teamPath;
    @Column(name = "PARENT_TEAM_PK", nullable = true, columnDefinition = "bigint default null")
    private Long parentTeamPk;


    @Column(name = "CREATED_BY", nullable = false, columnDefinition = "bigint default 0")
    @ColumnDefault("0")
    private Long createdBy;
    @Column(name = "CREATED_DATE", nullable = false, columnDefinition = "datetime2(0) default CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Timestamp createdDate;
    @Column(name = "UPDATED_BY", nullable = false, columnDefinition = "bigint default 0")
    @ColumnDefault("0")
    private Long updatedBy;
    @Column(name = "UPDATED_DATE", nullable = false, columnDefinition = "datetime2(0) default CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private Timestamp updatedDate;

    @JsonInclude(NON_EMPTY)
    @Where(clause = "USE_YN=1")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Team.class)
    @JoinColumn(name = "PARENT_TEAM_PK", insertable = false, updatable = false)
    private List<Team> childrenTeam = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team that = (Team) o;
        return useYn == that.useYn && Objects.equals(id, that.id) && Objects.equals(tenantPk, that.tenantPk) && Objects.equals(visibleYn, that.visibleYn) && Objects.equals(teamName, that.teamName) && Objects.equals(order, that.order) && teamLevel == that.teamLevel && Objects.equals(teamPath, that.teamPath) && Objects.equals(parentTeamPk, that.parentTeamPk) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(updatedDate, that.updatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantPk, useYn, visibleYn, teamName, order, teamLevel, teamPath, parentTeamPk, createdBy, createdDate, updatedBy, updatedDate);
    }


    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", tenantPk=" + tenantPk +
                ", useYn=" + useYn +
                ", visibleYn=" + visibleYn +
                ", teamName='" + teamName + '\'' +
                ", order=" + order +
                ", teamLevel=" + teamLevel +
                ", teamPath='" + teamPath + '\'' +
                ", parentTeamPk=" + parentTeamPk +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", updatedBy=" + updatedBy +
                ", updatedDate=" + updatedDate +
                '}';
    }

    public boolean hasParentTeam() {
        return parentTeamPk != null;
    }
}
