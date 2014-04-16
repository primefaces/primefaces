<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
  - Stylesheet to pretty-print an xml document. In addition, if the input
  - document contains any xml entity references, then those are expanded
  - inline.
  -
  - Based on a stylesheet by John Mongan.
  -->
<xsl:stylesheet version="1.1"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml"
                encoding="ISO-8859-1"/>

    <xsl:param name="indent-increment" select="'   '" />

    <xsl:template match="*">
      <xsl:param name="indent" select="'&#xA;'"/>

      <xsl:value-of select="$indent"/>
      <xsl:copy>
        <xsl:copy-of select="@*" />
        <xsl:apply-templates>
          <xsl:with-param name="indent"
               select="concat($indent, $indent-increment)"/>
        </xsl:apply-templates>
        <xsl:if test="*">
          <xsl:value-of select="$indent"/>
        </xsl:if>
      </xsl:copy>
    </xsl:template>

   <xsl:template match="comment()|processing-instruction()">
      <xsl:param name="indent" select="'&#xA;'"/>
      <xsl:value-of select="$indent"/>
      <xsl:copy />
   </xsl:template>

   <!-- 
     - Discard the text content of a node where the text content
     - is nothing but whitespace. When this rule isn't matched
     - the default xml processing applies, which is to copy the
     - text directly to the output.
     -
     - WARNING: this is dangerous. Handle with care 
     -->
   <xsl:template match="text()[normalize-space(.)='']"/>

</xsl:stylesheet>
