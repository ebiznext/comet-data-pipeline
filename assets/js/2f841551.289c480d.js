(window.webpackJsonp=window.webpackJsonp||[]).push([[16],{134:function(e,t,n){"use strict";n.d(t,"a",(function(){return p})),n.d(t,"b",(function(){return d}));var a=n(0),r=n.n(a);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},o=Object.keys(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var u=r.a.createContext({}),s=function(e){var t=r.a.useContext(u),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=s(e.components);return r.a.createElement(u.Provider,{value:t},e.children)},m={inlineCode:"code",wrapper:function(e){var t=e.children;return r.a.createElement(r.a.Fragment,{},t)}},E=r.a.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,l=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),p=s(n),E=a,d=p["".concat(l,".").concat(E)]||p[E]||m[E]||o;return n?r.a.createElement(d,i(i({ref:t},u),{},{components:n})):r.a.createElement(d,i({ref:t},u))}));function d(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,l=new Array(o);l[0]=E;var i={};for(var c in t)hasOwnProperty.call(t,c)&&(i[c]=t[c]);i.originalType=e,i.mdxType="string"==typeof e?e:a,l[1]=i;for(var u=2;u<o;u++)l[u]=n[u];return r.a.createElement.apply(null,l)}return r.a.createElement.apply(null,n)}E.displayName="MDXCreateElement"},86:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return l})),n.d(t,"metadata",(function(){return i})),n.d(t,"toc",(function(){return c})),n.d(t,"default",(function(){return s}));var a=n(3),r=n(7),o=(n(0),n(134)),l={sidebar_position:1},i={unversionedId:"howto/extract",id:"howto/extract",isDocsHomePage:!1,title:"Extract",description:"This feature allows you to extract files from any JDBC compliant database to comma delimited values files.",source:"@site/docs/howto/extract.md",sourceDirName:"howto",slug:"/howto/extract",permalink:"/comet-data-pipeline/docs/howto/extract",editUrl:"https://github.com/facebook/docusaurus/edit/master/website/docs/howto/extract.md",version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"cometSidebar",previous:{title:"Configuration",permalink:"/comet-data-pipeline/docs/userguide/configuration"},next:{title:"Load",permalink:"/comet-data-pipeline/docs/howto/load"}},c=[{value:"The YAML File",id:"the-yaml-file",children:[]},{value:"The Mustache Template",id:"the-mustache-template",children:[]},{value:"The comet extract command",id:"the-comet-extract-command",children:[]}],u={toc:c};function s(e){var t=e.components,n=Object(r.a)(e,["components"]);return Object(o.b)("wrapper",Object(a.a)({},u,n,{components:t,mdxType:"MDXLayout"}),Object(o.b)("p",null,"This feature allows you to extract files from any JDBC compliant database to comma delimited values files."),Object(o.b)("p",null,"To extract a table of view content, we need to :"),Object(o.b)("ul",null,Object(o.b)("li",{parentName:"ul"},"Write a YAML file that describe the table schema"),Object(o.b)("li",{parentName:"ul"},"a mustache template that describe how the table data should be extracted as files. A generic mustache template is provided below"),Object(o.b)("li",{parentName:"ul"},"Run ",Object(o.b)("inlineCode",{parentName:"li"},"comet extract")," to apply the templated script to your database")),Object(o.b)("h2",{id:"the-yaml-file"},"The YAML File"),Object(o.b)("p",null,"The extract and load process are both based on the same YAML description file.\nPlease check first how a schema is described in See :ref:",Object(o.b)("inlineCode",{parentName:"p"},"howto_load")),Object(o.b)("p",null,"The only difference is that the YAML file for the extraction process describe a table schema instead of a formatted file schema.\nIn that case, the following YAML fields have a special meaning:"),Object(o.b)("ul",null,Object(o.b)("li",{parentName:"ul"},"Domain Name : Database schema"),Object(o.b)("li",{parentName:"ul"},"Schema Name : Table name")),Object(o.b)("h2",{id:"the-mustache-template"},"The Mustache Template"),Object(o.b)("p",null,"Write a mustache template that run a SQL export request to the target file.\nThe following parameters are available :"),Object(o.b)("ul",null,Object(o.b)("li",{parentName:"ul"},"full_export : Boolean. If true means that we are requesting a full export"),Object(o.b)("li",{parentName:"ul"},"export_file : filename of the exported data"),Object(o.b)("li",{parentName:"ul"},"table_name : table name in uppercase"),Object(o.b)("li",{parentName:"ul"},"delimiter : delimiter to use in the export file"),Object(o.b)("li",{parentName:"ul"},"columns : Column map with the single name attribute")),Object(o.b)("pre",null,Object(o.b)("code",{parentName:"pre",className:"language-sql"},"    -- How the data should be exported\n    SET ECHO OFF\n    SET VERIFY OFF\n    SET TRIMSPOOL ON\n    SET TRIMOUT ON\n    SET LINESIZE 9999\n    SET PAGESIZE 0\n    -- We decide to export without any header.\n    -- Do not forget to value the withHeader property to false in the YAML file\n    SET HEADING OFF\n    SET FEEDBACK OFF\n    SET TIMING OFF\n    SET TIME OFF\n    SET LONG 10000\n    -- The separator here should be the one used in the YAML file\n    SET COLSEP ';'\n    SET HEADSEP off\n\n    -- Stop in case of failure\n    WHENEVER SQLERROR exit 1;\n\n    -- Data time pattern we want to use\n    ALTER SESSION SET NLS_DATE_FORMAT = 'yyyymmddhh24miss';\n\n    -- The output file directory\n    DEFINE OUTPUT_DIR = &1;\n\n    -- Get current date/time.\n    COLUMN DT_VAL NEW_VALUE CURRENT_EXPORT_DATE_CHAR;\n    SELECT TO_CHAR(SYSDATE) DT_VAL FROM DUAL;\n\n    -- We store in a dedicated table, the last export date/time.\n    -- Useful for incremental exports\n    COLUMN LED NEW_VALUE LAST_EXPORT_DATE;\n    SELECT\n        COALESCE(\n            (\n                SELECT\n                    MAX(DA_LAST_EXPORT_DATE)\n                FROM\n                    MY_SCHEMA.COMET_EXPORT_STATUS\n                WHERE\n                    LI_SCHEMA_NAME = 'MY_SCHEMA' AND\n                    LI_TABLE_NAME = '{{table_name}}'\n            ),\n            TO_DATE('19700101','yyyymmdd') -- If table has never been exported\n        ) LED\n    FROM\n        DUAL;\n\n    -- Start export\n    PROMPT EXPORTING {{table_name}} TO &OUTPUT_DIR/{{export_file}}_{{#full_export}}FULL{{/full_export}}{{^full_export}}DELTA{{/full_export}}_&CURRENT_EXPORT_DATE_CHAR\\.csv;\n    SPOOL &OUTPUT_DIR/{{export_file}}_{{#full_export}}FULL{{/full_export}}{{^full_export}}DELTA{{/full_export}}_&CURRENT_EXPORT_DATE_CHAR\\.csv REPLACE\n\n    ALTER SESSION SET NLS_DATE_FORMAT = 'yyyy-mm-dd hh24:mi:ss';\n\n    -- SQL to execute if an incremental export is requested\n    {{^full_export}}\n    SELECT\n        {{#columns}}\n        TO_CHAR({{name}}) || ';' ||\n        {{/columns}}\n        ''\n    FROM\n        MY_SCHEMA.{{table_name}}\n    WHERE\n        {{delta_column}} >= '&LAST_EXPORT_DATE' AND {{delta_column}} IS NOT NULL;\n    {{/full_export}}\n\n    -- SQL to execute if a full export is requested\n    {{#full_export}}\n    SELECT\n        {{#columns}}\n        TO_CHAR({{name}}) || ';' ||\n        {{/columns}}\n        ''\n    FROM\n        MY_SCHEMA.{{table_name}};\n    {{/full_export}}\n\n    -- Export finished successfully\n    SPOOL OFF\n\n    -- Update reporot table containing the last expoort date\n    -- This is useful for audit purpose and for incremental export since we store the last export date here.\n    BEGIN\n        INSERT INTO\n            MY_SCHEMA.COMET_EXPORT_STATUS (LI_SCHEMA_NAME, LI_TABLE_NAME, DA_LAST_EXPORT_DATE, TYPE_LAST_EXPORT, NB_ROWS_LAST_EXPORT)\n        VALUES\n            (\n                'MY_SCHEMA',\n                '{{table_name}}',\n                TO_DATE(&CURRENT_EXPORT_DATE_CHAR),\n                {{#full_export}}\n                'FULL',\n                (\n                    SELECT\n                        COUNT(*)\n                    FROM\n                        MY_SCHEMA.{{table_name}}\n                )\n                {{/full_export}}\n                {{^full_export}}\n                'DELTA',\n                (\n                    SELECT\n                        COUNT(*)\n                    FROM\n                        MY_SCHEMA.{{table_name}}\n                    WHERE\n                        {{delta_column}} >= '&LAST_EXPORT_DATE' AND {{delta_column}} IS NOT NULL\n                )\n                {{/full_export}}\n            );\n    END;\n    /\n\n    EXIT SUCCESS\n\n    sqlplus sys/Ora_db1 as SYSDBA @ EXTRACT_{{table_name}}.sql /opt/oracle/user-scripts/scripts/\n")),Object(o.b)("h2",{id:"the-comet-extract-command"},"The comet extract command"),Object(o.b)("p",null,Object(o.b)("a",{parentName:"p",href:"/comet-data-pipeline/docs/cli/import"},"CLI")))}s.isMDXComponent=!0}}]);