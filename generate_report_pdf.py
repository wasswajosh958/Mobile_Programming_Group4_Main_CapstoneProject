from pathlib import Path

SRC = Path("PROJECT_PROCESS_REPORT.md")
OUT = Path("PROJECT_PROCESS_REPORT.pdf")

text = SRC.read_text(encoding="utf-8")

try:
    from reportlab.lib.pagesizes import A4
    from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
    from reportlab.lib.units import cm
    from reportlab.platypus import Paragraph, SimpleDocTemplate, Spacer
except Exception:
    import subprocess
    import sys

    subprocess.check_call([sys.executable, "-m", "pip", "install", "reportlab", "--quiet"])
    from reportlab.lib.pagesizes import A4
    from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
    from reportlab.lib.units import cm
    from reportlab.platypus import Paragraph, SimpleDocTemplate, Spacer

styles = getSampleStyleSheet()
body = ParagraphStyle("Body", parent=styles["Normal"], fontSize=10.5, leading=14)
head = ParagraphStyle("Head", parent=styles["Heading2"], fontSize=13, leading=16, spaceAfter=8)

doc = SimpleDocTemplate(
    str(OUT),
    pagesize=A4,
    leftMargin=2 * cm,
    rightMargin=2 * cm,
    topMargin=1.5 * cm,
    bottomMargin=1.5 * cm,
)
flow = []

for raw_line in text.splitlines():
    line = raw_line.strip()
    if not line:
        flow.append(Spacer(1, 6))
        continue
    if line.startswith("# "):
        flow.append(Paragraph(line[2:], styles["Title"]))
        flow.append(Spacer(1, 10))
    elif line.startswith("## "):
        flow.append(Paragraph(line[3:], head))
    elif line.startswith("- "):
        flow.append(Paragraph(f"• {line[2:]}", body))
    else:
        safe = line.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        flow.append(Paragraph(safe, body))

doc.build(flow)
print(f"PDF generated: {OUT.resolve()}")
