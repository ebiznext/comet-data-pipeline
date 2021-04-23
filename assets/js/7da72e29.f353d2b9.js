(window.webpackJsonp=window.webpackJsonp||[]).push([[30],{100:function(e,t,a){"use strict";a.r(t),a.d(t,"frontMatter",(function(){return i})),a.d(t,"metadata",(function(){return s})),a.d(t,"toc",(function(){return u})),a.d(t,"default",(function(){return l}));var r=a(3),n=a(7),o=(a(0),a(134)),i={sidebar_position:3,title:"Transform"},s={unversionedId:"examples/transform",id:"examples/transform",isDocsHomePage:!1,title:"Transform",description:"Parquet to Parquet",source:"@site/docs/examples/transform.md",sourceDirName:"examples",slug:"/examples/transform",permalink:"/comet-data-pipeline/docs/examples/transform",editUrl:"https://github.com/facebook/docusaurus/edit/master/website/docs/examples/transform.md",version:"current",sidebarPosition:3,frontMatter:{sidebar_position:3,title:"Transform"},sidebar:"cometSidebar",previous:{title:"Load",permalink:"/comet-data-pipeline/docs/examples/load"},next:{title:"Microsoft Azure",permalink:"/comet-data-pipeline/docs/cloud/azure"}},u=[{value:"Parquet to Parquet",id:"parquet-to-parquet",children:[]},{value:"Transform Parquet to DSV",id:"transform-parquet-to-dsv",children:[]},{value:"Transform Parquet to BigQuery",id:"transform-parquet-to-bigquery",children:[]},{value:"BigQuery to BigQuery",id:"bigquery-to-bigquery",children:[]},{value:"BigQuery to CSV",id:"bigquery-to-csv",children:[]},{value:"BigQuery to Parquet",id:"bigquery-to-parquet",children:[]},{value:"Parquet to Elasticsearch",id:"parquet-to-elasticsearch",children:[]},{value:"BigQuery to Elasticsearch",id:"bigquery-to-elasticsearch",children:[]},{value:"BigQuery to SQL Database",id:"bigquery-to-sql-database",children:[]},{value:"Parquet to SQL Database",id:"parquet-to-sql-database",children:[]},{value:"SQL Database to SQL Database",id:"sql-database-to-sql-database",children:[]}],c={toc:u};function l(e){var t=e.components,a=Object(n.a)(e,["components"]);return Object(o.b)("wrapper",Object(r.a)({},c,a,{components:t,mdxType:"MDXLayout"}),Object(o.b)("h2",{id:"parquet-to-parquet"},"Parquet to Parquet"),Object(o.b)("p",null,"Will load the dataset ",Object(o.b)("inlineCode",{parentName:"p"},"accepted/graduateProgram")," under ",Object(o.b)("inlineCode",{parentName:"p"},"$COMET_DATASETS")," directory from the configured storage.\nAn absolute path may also be specified."),Object(o.b)("p",null,"This example create two views : One temporary view in the ",Object(o.b)("inlineCode",{parentName:"p"},"views")," section, and another one in the ",Object(o.b)("inlineCode",{parentName:"p"},"presql")," section.\nNote that the sql request in the ",Object(o.b)("inlineCode",{parentName:"p"},"presql")," section uses the view defined in the ",Object(o.b)("inlineCode",{parentName:"p"},"views")," sectioon."),Object(o.b)("p",null,"The resulting file will be stored in the ",Object(o.b)("inlineCode",{parentName:"p"},"$COMET_DATASETS/business/graduateProgram/output")," directory."),Object(o.b)("pre",null,Object(o.b)("code",{parentName:"pre",className:"language-yaml"},'---\nname: "graduateProgram"\nviews:\n  graduate_View: "fs:accepted/graduateProgram"\ntasks:\n  - domain: "graduateProgram"\n    area: "business"\n    dataset: "output"\n    write: "OVERWRITE"\n    presql: |\n      create or replace view graduate_agg_view\n      select degree,\n        department,\n        school\n      from graduate_View\n      where school={{school}}\n\n    sql: SELECT * FROM graduate_agg_view\n')),Object(o.b)("h2",{id:"transform-parquet-to-dsv"},"Transform Parquet to DSV"),Object(o.b)("p",null,"Based ont the :ref:",Object(o.b)("inlineCode",{parentName:"p"},"parquet_to_parquet")," example, we add the format property to request a csv output\nand set coalesce to ",Object(o.b)("inlineCode",{parentName:"p"},"true")," to output everything in a single CSV file."),Object(o.b)("pre",null,Object(o.b)("code",{parentName:"pre",className:"language-yaml"},'---\nname: "graduateProgram"\nformat: "csv"\ncoalesce: true\nviews:\n  graduate_View: "fs:accepted/graduateProgram"\ntasks:\n  - domain: "graduateProgram"\n    area: "business"\n    dataset: "output"\n    write: "OVERWRITE"\n    presql: |\n      create or replace view graduate_agg_view\n      select degree,\n        department,\n        school\n      from graduate_View\n      where school={{school}}\n\n    sql: SELECT * FROM graduate_agg_view\n')),Object(o.b)("h2",{id:"transform-parquet-to-bigquery"},"Transform Parquet to BigQuery"),Object(o.b)("p",null,"Based ont the :ref:",Object(o.b)("inlineCode",{parentName:"p"},"parquet_to_parquet")," example, we add the sink section to force the task to store the SQL result in BigQuery"),Object(o.b)("p",null,"The result will store in the current project under the ",Object(o.b)("inlineCode",{parentName:"p"},"business")," BigQuery dataset in the ",Object(o.b)("inlineCode",{parentName:"p"},"output")," table."),Object(o.b)("p",null,"You may also specify the target project in the ",Object(o.b)("inlineCode",{parentName:"p"},"/tasks/dataset")," property using the syntax ",Object(o.b)("inlineCode",{parentName:"p"},"PROJECT_ID:business")),Object(o.b)("pre",null,Object(o.b)("code",{parentName:"pre",className:"language-yaml"},'---\nname: "graduateProgram"\nviews:\n  graduate_View: "fs:accepted/graduateProgram"\ntasks:\n  - domain: "graduateProgram"\n    area: "business"\n    dataset: "output"\n    write: "OVERWRITE"\n    sink:\n        type: BQ\n        location: EU\n    presql: |\n      create or replace view graduate_agg_view\n      select degree,\n        department,\n        school\n      from graduate_View\n      where school={{school}}\n\n    sql: SELECT * FROM graduate_agg_view\n')),Object(o.b)("h2",{id:"bigquery-to-bigquery"},"BigQuery to BigQuery"),Object(o.b)("p",null,"We may use the Spark (SPARK) or BigQuery (BQ) engine. When using the BQ engine, no spark cluster is needed."),Object(o.b)("p",null,"You may want to use the Spark engine if you need to run your jobs to stay agnostic to the underlying storage or\nif you need your jobs to overwrite only the partitions present in the resulting SQL."),Object(o.b)("pre",null,Object(o.b)("code",{parentName:"pre",className:"language-yaml"},'---\nname: "graduateProgram"\nviews:\n  graduate_View: "bq:gcp_project_id:bqdataset/graduateProgram"\ntasks:\n  - domain: "graduateProgram"\n    sink:\n        type: BQ\n    area: "business"\n    dataset: "output"\n    write: "OVERWRITE"\n    sql: SELECT * FROM graduate_View\n')),Object(o.b)("h2",{id:"bigquery-to-csv"},"BigQuery to CSV"),Object(o.b)("h2",{id:"bigquery-to-parquet"},"BigQuery to Parquet"),Object(o.b)("h2",{id:"parquet-to-elasticsearch"},"Parquet to Elasticsearch"),Object(o.b)("h2",{id:"bigquery-to-elasticsearch"},"BigQuery to Elasticsearch"),Object(o.b)("h2",{id:"bigquery-to-sql-database"},"BigQuery to SQL Database"),Object(o.b)("h2",{id:"parquet-to-sql-database"},"Parquet to SQL Database"),Object(o.b)("h2",{id:"sql-database-to-sql-database"},"SQL Database to SQL Database"))}l.isMDXComponent=!0},134:function(e,t,a){"use strict";a.d(t,"a",(function(){return p})),a.d(t,"b",(function(){return m}));var r=a(0),n=a.n(r);function o(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function i(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,r)}return a}function s(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?i(Object(a),!0).forEach((function(t){o(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function u(e,t){if(null==e)return{};var a,r,n=function(e,t){if(null==e)return{};var a,r,n={},o=Object.keys(e);for(r=0;r<o.length;r++)a=o[r],t.indexOf(a)>=0||(n[a]=e[a]);return n}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)a=o[r],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(n[a]=e[a])}return n}var c=n.a.createContext({}),l=function(e){var t=n.a.useContext(c),a=t;return e&&(a="function"==typeof e?e(t):s(s({},t),e)),a},p=function(e){var t=l(e.components);return n.a.createElement(c.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.a.createElement(n.a.Fragment,{},t)}},b=n.a.forwardRef((function(e,t){var a=e.components,r=e.mdxType,o=e.originalType,i=e.parentName,c=u(e,["components","mdxType","originalType","parentName"]),p=l(a),b=r,m=p["".concat(i,".").concat(b)]||p[b]||d[b]||o;return a?n.a.createElement(m,s(s({ref:t},c),{},{components:a})):n.a.createElement(m,s({ref:t},c))}));function m(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=a.length,i=new Array(o);i[0]=b;var s={};for(var u in t)hasOwnProperty.call(t,u)&&(s[u]=t[u]);s.originalType=e,s.mdxType="string"==typeof e?e:r,i[1]=s;for(var c=2;c<o;c++)i[c]=a[c];return n.a.createElement.apply(null,i)}return n.a.createElement.apply(null,a)}b.displayName="MDXCreateElement"}}]);